package com.orchestranetworks.ps.ranges;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.util.functional.*;
import com.orchestranetworks.schema.*;

/**
 * For a range, which is an object with a pair of like-typed, comparable values, this class provides some utilities
 * for comparing two ranges, seeing if they overlap or leave a gap.
 */
public class RangeUtils
{
	public static int ONE_DAY_IN_MILLIS = 60 * 60 * 24 * 1000;
	public static int ONE_SECOND_IN_MILLIS = 1000;
	public static int ONE = 1;
	public static float ONE_HUNDREDTH = 1 / 100;

	public static enum State {
		Overlap, Gap
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static RangeInfo<Adaptation> checkRanges(
		List<Adaptation> ranges,
		Path minPath,
		Path maxPath,
		Adaptation existing,
		Comparable newMinValue,
		Comparable newMaxValue,
		boolean checkOverlaps,
		boolean checkGaps,
		float paddingNum)
	{
		UnaryFunction<Adaptation, Comparable> getMin = createGetValue(
			existing,
			newMinValue,
			minPath);
		UnaryFunction<Adaptation, Comparable> getMax = createGetValue(
			existing,
			newMaxValue,
			maxPath);
		return checkRanges(ranges, getMin, getMax, checkOverlaps, checkGaps, paddingNum);
	}

	public static <T, C extends Comparable<C>> RangeInfo<T> checkRanges(
		List<T> ranges,
		UnaryFunction<T, C> getMin,
		UnaryFunction<T, C> getMax,
		boolean checkOverlap,
		boolean checkGap,
		float paddingNum)
	{
		sortRanges(ranges, getMin);
		T prev = null;
		for (T range : ranges)
		{
			if (prev != null)
			{
				RangeInfo<T> state = getState(
					prev,
					range,
					getMin,
					getMax,
					checkOverlap,
					checkGap,
					paddingNum);
				if (state != null)
				{
					return state;
				}
			}
			prev = range;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static UnaryFunction<Adaptation, Comparable> createGetValue(
		final Adaptation existing,
		final Comparable newValue,
		final Path valuePath)
	{
		return new UnaryFunction<Adaptation, Comparable>()
		{
			@Override
			public Comparable evaluate(Adaptation record)
			{
				if (record == null)
					return null;
				if (record.equalsToAdaptation(existing))
					return newValue;
				return (Comparable) record.get(valuePath);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T, C extends Comparable<C>> RangeInfo<T> getState(
		T range1,
		T range2,
		UnaryFunction<T, C> getMin,
		UnaryFunction<T, C> getMax,
		boolean checkOverlap,
		boolean checkGap,
		float paddingNum)
	{
		C min1 = getMin.evaluate(range1);
		C min2 = getMin.evaluate(range2);
		if ((min1 != null && min2 != null && min1.compareTo(min2) > 0)
			|| (min1 != null && min2 == null))
		{ //swap
			T temp = range1;
			range1 = range2;
			range2 = temp;
			min1 = min2;
			min2 = getMin.evaluate(range2);
		}
		C max1 = getMax.evaluate(range1);
		C max2 = getMax.evaluate(range2);
		if (checkOverlap && (min1 == null || max2 == null || min1.compareTo(max2) < 0)
			&& (max1 == null || min2 == null || max1.compareTo(min2) > 0))
			return new RangeInfo<T>(State.Overlap, range1, range2);
		if (checkGap && min2 != null && max1 != null
			&& padMin(min2, paddingNum).compareTo(max1) > 0)
			return new RangeInfo<T>(State.Gap, range1, range2);
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Comparable padMin(Comparable minVal, float paddingNum)
	{
		if (paddingNum > 0)
		{
			if (minVal instanceof Date)
			{
				long dateInMillis = ((Date) minVal).getTime();
				return new Date(dateInMillis - (long) paddingNum);
			}
			else if (minVal instanceof Integer)
			{
				return Integer.valueOf(((Integer) minVal).intValue() - (int) paddingNum);
			}
			else if (minVal instanceof Double)
			{
				return Double.valueOf(((Double) minVal).doubleValue() - paddingNum);
			}
		}
		return minVal;
	}

	@SuppressWarnings("rawtypes")
	public static Comparable padMax(Comparable maxVal, float paddingNum)
	{
		if (paddingNum > 0)
		{
			if (maxVal instanceof Date)
			{
				long dateInMillis = ((Date) maxVal).getTime();
				return new Date(dateInMillis + (long) paddingNum);
			}
			else if (maxVal instanceof Integer)
			{
				return Integer.valueOf(((Integer) maxVal).intValue() + (int) paddingNum);
			}
			else if (maxVal instanceof Double)
			{
				return Double.valueOf(((Double) maxVal).doubleValue() + paddingNum);
			}
		}
		return maxVal;
	}

	public static <T, C extends Comparable<C>> void sortRanges(
		List<T> ranges,
		UnaryFunction<T, C> getMin)
	{
		Collections.sort(ranges, new RangeComparator<T, C>(getMin));
	}

	public static class RangeComparator<T, C extends Comparable<C>> implements Comparator<T>
	{
		private final UnaryFunction<T, C> getMin;

		public RangeComparator(UnaryFunction<T, C> getMin)
		{
			this.getMin = getMin;
		}

		@Override
		public int compare(T o1, T o2)
		{
			C min1 = getMin.evaluate(o1);
			C min2 = getMin.evaluate(o2);
			if (min1 == null)
			{
				if (min2 == null)
					return 0;
				return -1;
			}
			else if (min2 == null)
			{
				return 1;
			}
			return min1.compareTo(min2);
		}
	}

	public static class RangeInfo<T>
	{
		private final State state;
		private final T object;
		private final T otherObject;
		public RangeInfo(State state, T object, T otherObject)
		{
			super();
			this.state = state;
			this.object = object;
			this.otherObject = otherObject;
		}
		public State getState()
		{
			return state;
		}
		public T getObject()
		{
			return object;
		}
		public T getOtherObject()
		{
			return otherObject;
		}
	}
}
