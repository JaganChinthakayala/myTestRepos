package com.hertz.mdm.vehicle.scripttask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads Chrome Image Gallery Mapping files, filtering by Type, Background and
 * Size, grouping by StyleID and ImageID.
 *
 * @author pbranch
 *
 */
/* package */ class ImageGalleryMappingFileReader
{
	private static final Pattern FIELD_PATTERN = Pattern.compile("(~([^~]*)~|[^,]*),?");

	private static final String SIZE_FILTER = "640";
	private static final String BACKGROUND_FILTER = "Transparent";
	private static final String TYPE_FILTER = "MultiView";

	static final int STYLE_ID = 0;
	static final int IMAGE_ID = 1;
	static final int FILE_NAME = 2;
	static final int TYPE = 3;
	static final int BACKGROUND = 4;
	static final int SIZE = 5;

	public final String inputDirectory;

	public ImageGalleryMappingFileReader(String inputDirectory)
	{
		this.inputDirectory = trimTrailingSlashes(inputDirectory);
	}

	public Set<ImageGalleryMappingRecord> readFile(String country, int year)
	{
		Set<ImageGalleryMappingRecord> records = new LinkedHashSet<ImageGalleryMappingRecord>();
		String filepath = String.format(
			"%s/NVD_Fleet_%s_EN_%04d/%s_ImageGalleryMapping_%04d_Vehicle.txt",
			inputDirectory,
			country,
			year,
			country,
			year);
		File file = new File(filepath);

		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				String[] values = extractValues(line);
				if (!(values[TYPE].equals(TYPE_FILTER) && values[BACKGROUND].equals(BACKGROUND_FILTER)
					&& values[SIZE].equals(SIZE_FILTER)))
				{
					continue;
				}
				ImageGalleryMappingRecord record = new ImageGalleryMappingRecord(values[STYLE_ID], values[IMAGE_ID], values[FILE_NAME]);
				records.add(record);
			}
		}
		catch (IOException e)
		{
			// TODO Log exception
		}

		return records;
	}

	public String[] extractValues(String string)
	{
		// STYLE_ID, IMAGE_ID, FILE_NAME, TYPE, BACKGROUND, SIZE
		String[] values = new String[6];

		// Comma delimited values. Strings wrapped w/ '~' character
		Matcher m = FIELD_PATTERN.matcher(string);
		for (int i = 0; i < values.length && m.find(); ++i)
		{
			String insideTildes = m.group(2);
			if (insideTildes != null)
			{
				// Match found wrapped w/ '~'
				values[i] = insideTildes;
			}
			else
			{
				// No '~' so use first match
				values[i] = m.group(1);
			}
		}

		return values;
	}

	private String trimTrailingSlashes(String fullString)
	{
		int end = fullString.length();
		while (end > 0 && fullString.charAt(end - 1) == '/')
		{
			--end;
		}
		return fullString.substring(0, end);
	}

	public static class ImageGalleryMappingRecord
	{
		public final String styleId;
		public final String imageId;
		public final String fileName;

		private final String unique;

		public ImageGalleryMappingRecord(String styleId, String imageId, String fileName)
		{
			this.styleId = styleId;
			this.imageId = imageId;
			this.fileName = fileName;

			this.unique = styleId + "," + imageId + "," + fileName;
		}

		@Override
		public String toString()
		{
			return this.unique;
		}

		@Override
		public int hashCode()
		{
			return unique.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj != null && obj instanceof ImageGalleryMappingRecord)
			{
				ImageGalleryMappingRecord other = (ImageGalleryMappingRecord)obj;
				return unique.equals(other.unique);
			}
			return false;
		}
	}
}