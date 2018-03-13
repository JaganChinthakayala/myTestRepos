package com.orchestranetworks.ps.constraint;

import java.text.*;
import java.util.*;

import org.apache.commons.lang.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

public class RecordInTableConstraintEnumeration implements ConstraintEnumeration
{

	public static final String MESSAGE = "{0} should specify a record in the {1} Table";
	public static final String GMESSAGE = "{0} should specify a record in the referenced table";

	private Path tableField;
	private String pathsAsString;
	private Path[] paths;
	private SchemaNode recordField;

	public Path getTableField()
	{
		return tableField;
	}

	public void setTableField(Path tableField)
	{
		this.tableField = tableField;
	}

	public String getPathToTableField()
	{
		return pathsAsString;
	}

	public void setPathToTableField(String pathsAsString)
	{
		this.pathsAsString = pathsAsString;
	}

	@Override
	public void checkOccurrence(Object value, ValueContextForValidation context)
		throws InvalidSchemaException
	{
		String recordKey = (String) value;
		try
		{
			AdaptationTable table = getTable(context, null);
			if (table == null)
				return;
			Adaptation record = table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(recordKey));
			if (record == null)
			{
				context.addError(MessageFormat.format(
					MESSAGE,
					table.getTableNode().getLabel(Locale.getDefault())));
			}
		}
		catch (OperationException e)
		{
			context.addError("Failed to Reference Table for record lookup");
		}
	}

	public Adaptation getDataSet(ValueContext context)
	{
		return context.getAdaptationTable().getContainerAdaptation();
	}

	private AdaptationTable getTable(ValueContext context, String tableName)
		throws OperationException
	{
		if (tableName == null)
			tableName = getTableName(context);
		Adaptation dataSet = getDataSet(context);
		try
		{
			return dataSet.getTable(Path.parse(tableName));
		}
		catch (PathAccessException e)
		{
			return null;
		}
	}

	public String getTableName(ValueContext context)
	{
		if (paths == null)
			return (String) context.getValue(Path.PARENT.add(tableField));
		else
			return (String) CollectionUtils.getFirstOrNull(AdaptationUtil.evaluatePath(
				context,
				paths,
				tableField,
				true));
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return MessageFormat.format(GMESSAGE, recordField.getLabel(locale));
	}

	@Override
	public String displayOccurrence(Object value, ValueContext context, Locale locale)
		throws InvalidSchemaException
	{
		return getLabelForRecord(context, (String) value, locale);
	}

	public String getLabelForRecord(ValueContext context, String recordKey, Locale locale)
	{
		if (StringUtils.isEmpty(recordKey))
			return null;
		try
		{
			AdaptationTable table = getTable(context, null);
			if (table != null)
			{
				Adaptation record = table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(recordKey));
				if (record == null)
					return recordKey;
				return record.getLabel(locale);
			}
			return recordKey;
		}
		catch (OperationException e)
		{
			return recordKey + "(! error computing label)";
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getValues(ValueContext context) throws InvalidSchemaException
	{
		try
		{
			AdaptationTable table = getTable(context, getTableName(context));
			RequestResult allRows = table.createRequestResult(null);
			List<String> keys = new ArrayList<String>();
			Adaptation next = allRows.nextAdaptation();
			while (next != null)
			{
				keys.add(next.getOccurrencePrimaryKey().format());
				next = allRows.nextAdaptation();
			}
			return keys;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public void setup(ConstraintContext context)
	{
		SchemaNode node = context.getSchemaNode();
		SchemaNode root = node.getTableNode().getTableOccurrenceRootNode();
		if (pathsAsString != null)
		{
			List<Path> pathList = PathUtils.convertStringToPathList(pathsAsString, null);
			List<SchemaNode> nodes = PathUtils.validatePath(root, pathList);
			if (nodes.size() < pathList.size())
			{
				context.addError("Path " + pathList.get(nodes.size()).format() + " not found.");
			}
			tableField = pathList.remove(pathList.size() - 1);
			if (!pathList.isEmpty())
			{
				this.paths = pathList.toArray(new Path[0]);
				this.paths[0] = Path.PARENT.add(this.paths[0]);
			}
			if (tableField == null)
				context.addError("TableField or PathToTableField is required");
		}
		else
		{
			if (tableField == null)
				context.addError("TableField is required");
			if (root.getNode(tableField) == null)
				context.addError("Field " + tableField.format() + " not found.");
		}
	}

}
