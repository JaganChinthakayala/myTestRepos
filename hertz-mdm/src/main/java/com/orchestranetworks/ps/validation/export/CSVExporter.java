/*
 * Copyright Orchestra Networks 2000-2011. All rights reserved.
 */
package com.orchestranetworks.ps.validation.export;

import java.io.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.export.*;
import com.orchestranetworks.ps.validation.bean.*;

public class CSVExporter extends FileExporter
{
	public static final String DEFAULT_EXPORT_FILE_NAME = "ValidationReport";
	public static final String DEFAULT_EXPORT_FILE_EXTENSION = ".csv";

	private List<ValidationErrorElement> errorList;
	private final String separator;
	private final boolean includePK;

	public CSVExporter(
		final String parentDir,
		final String exportDir,
		final String separator,
		final Locale locale) throws IOException
	{
		this(
			parentDir,
			exportDir,
			separator,
			locale,
			DEFAULT_EXPORT_FILE_NAME,
			DEFAULT_EXPORT_FILE_EXTENSION,
			false,
			false);
	}

	public CSVExporter(
		final String parentDir,
		final String exportDir,
		final String separator,
		final Locale locale,
		final String exportFileName,
		final String exportFileExtension,
		final boolean appendTimestamp,
		final boolean includePK) throws IOException
	{
		super(parentDir, exportDir, locale, exportFileName, exportFileExtension, appendTimestamp);
		this.separator = separator;
		this.includePK = includePK;
	}

	public void setValidationList(List<ValidationErrorElement> errorList)
	{
		this.errorList = errorList;
	}

	public String doExport() throws IOException
	{
		List<ValidationErrorElement> list = this.errorList;
		final BufferedWriter writer = new BufferedWriter(new FileWriter(this.exportFilePath));
		this.writeHeader(writer);

		for (final Iterator<ValidationErrorElement> iterator = list.iterator(); iterator.hasNext();)
		{
			final ValidationErrorElement validationErrorElement = iterator.next();
			final Adaptation record = validationErrorElement.getRecord();
			if (record.isTableOccurrence())
			{
				if (record.getContainerTable().getTableNode().getLabel(this.locale).contains(","))
				{
					writer.write(
						"\"" + record.getContainerTable().getTableNode().getLabel(this.locale)
							+ "\"");
				}
				else
				{
					writer.write(record.getContainerTable().getTableNode().getLabel(this.locale));
				}
				writer.write(separator);
				if (record.getLabel(this.locale).contains(","))
				{
					writer.write("\"" + record.getLabel(this.locale) + "\"");
				}
				else
				{
					writer.write(record.getLabel(this.locale));
				}
				writer.write(separator);
				if (includePK)
				{
					final String pkStr = record.getOccurrencePrimaryKey().format();
					if (pkStr.contains(","))
					{
						writer.write("\"" + pkStr + "\"");
					}
					else
					{
						writer.write(pkStr);
					}
					writer.write(separator);
				}
				if (validationErrorElement.getMessage().contains(","))
				{
					writer.write("\"" + validationErrorElement.getMessage() + "\"");
				}
				else
				{
					writer.write(validationErrorElement.getMessage());
				}
				writer.write(separator);
				writer.write(record.getLastUser().getUserId());
				writer.write(separator);
				writer.write(record.getTimeOfLastModification().toString());
				writer.newLine();
			}
			else
			{
				writer.write("Data Set");
				writer.write(separator);
				if (record.getLabelOrName(this.locale).contains(","))
				{
					writer.write("\"" + record.getLabelOrName(this.locale) + "\"");
				}
				else
				{
					writer.write(record.getLabelOrName(this.locale));
				}

				writer.write(separator);
				if (validationErrorElement.getMessage().contains(","))
				{
					writer.write("\"" + validationErrorElement.getMessage() + "\"");
				}
				else
				{
					writer.write(validationErrorElement.getMessage());
				}

				writer.newLine();
			}

		}
		writer.flush();
		writer.newLine();

		writer.close();
		return this.exportFilePath;
	}

	private void writeHeader(final BufferedWriter writer) throws IOException
	{
		writer.write("Table");
		writer.write(separator);
		writer.write("Record");
		writer.write(separator);
		if (includePK)
		{
			writer.write("Key");
			writer.write(separator);
		}
		writer.write("Message");
		writer.write(separator);
		writer.write("Last Modified User");
		writer.write(separator);
		writer.write("Last Modified Time");
		writer.flush();
		writer.newLine();
	}

}
