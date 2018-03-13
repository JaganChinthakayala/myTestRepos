package com.orchestranetworks.ps.validation.service;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.constants.*;
import com.orchestranetworks.ps.filetransfer.*;
import com.orchestranetworks.ps.validation.bean.*;
import com.orchestranetworks.ps.validation.export.*;
import com.orchestranetworks.service.*;

public class GenerateValidationReport
{
	public static final String DEFAULT_EXPORT_DIR_NAME = "csvExport";
	public static final String DEFAULT_FILE_DOWNLOADER_SERVLET = "/FileDownloader";

	private static final String CSV_SEPARATOR = ",";
	private static final String DEFAULT_PERMISSIONS_TEMPLATE_DATA_SPACE_NAME = "ValidationDataSpacePermissions";

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String exportParentDirName;
	protected String exportDirName;
	protected boolean appendTimestamp;
	protected boolean includePK;
	protected String fileDownloaderServlet;
	protected Severity minSeverity;
	protected boolean useChildDataSpace;
	protected String permissionsTemplateDataSpaceName = DEFAULT_PERMISSIONS_TEMPLATE_DATA_SPACE_NAME;

	public GenerateValidationReport(
		final HttpServletRequest request,
		final HttpServletResponse response)
	{
		this(
			request,
			response,
			request.getSession().getServletContext().getRealPath("/"),
			DEFAULT_EXPORT_DIR_NAME,
			false,
			false,
			DEFAULT_FILE_DOWNLOADER_SERVLET,
			Severity.ERROR,
			false);
	}

	public GenerateValidationReport(
		final HttpServletRequest request,
		final HttpServletResponse response,
		String exportParentDirName,
		String exportDirName,
		boolean appendTimestamp,
		boolean includePK,
		String fileDownloaderServlet,
		Severity minSeverity,
		boolean useChildDataSpace)
	{
		this.request = request;
		this.response = response;
		this.exportParentDirName = exportParentDirName;
		this.exportDirName = exportDirName;
		this.appendTimestamp = appendTimestamp;
		this.includePK = includePK;
		this.fileDownloaderServlet = fileDownloaderServlet;
		this.minSeverity = minSeverity;
		this.useChildDataSpace = useChildDataSpace;
	}

	public void execute() throws ServletException
	{
		final ServiceContext sContext = ServiceContext.getServiceContext(request);

		final ValidationReport validationReport;
		try
		{
			validationReport = generateValidationReport(sContext);
		}
		catch (OperationException e)
		{
			LoggingCategory.getKernel().error("Error occurred generating validation report.", e);
			throw new ServletException(e);
		}

		try
		{
			processValidationReport(sContext, validationReport);
		}
		catch (final IOException e)
		{
			LoggingCategory.getKernel().error("Error occurred processing validation report.", e);
			throw new ServletException(e);
		}
	}

	protected ValidationReport generateValidationReport(ServiceContext sContext)
		throws OperationException
	{
		ValidationReport validationReport;
		Adaptation dataSet = sContext.getCurrentAdaptation();
		AdaptationHome childDataSpace = null;
		try
		{
			if (useChildDataSpace)
			{
				childDataSpace = createChildDataSpace(sContext);
				dataSet = childDataSpace.findAdaptationOrNull(dataSet.getAdaptationName());
			}
			validationReport = dataSet.getValidationReport();
		}
		finally
		{
			if (useChildDataSpace && childDataSpace != null)
			{
				closeChildDataSpace(sContext, childDataSpace);
			}
		}
		return validationReport;
	}

	protected AdaptationHome createChildDataSpace(ServiceContext sContext)
		throws OperationException
	{
		Session session = sContext.getSession();
		AdaptationHome dataSpace = sContext.getCurrentHome();
		UserReference user = session.getUserReference();

		HomeCreationSpec homeCreationSpec = new HomeCreationSpec();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
			CommonConstants.DATA_SPACE_NAME_DATE_TIME_FORMAT);
		String childDataSpaceDateTimeStr = dateFormat.format(new Date());
		homeCreationSpec.setKey(HomeKey.forBranchName(childDataSpaceDateTimeStr));
		homeCreationSpec.setOwner(user);
		homeCreationSpec.setParent(dataSpace);
		homeCreationSpec.setLabel(UserMessage.createInfo("Validation Report for " + user.getLabel()
			+ " at " + childDataSpaceDateTimeStr));

		Repository repo = dataSpace.getRepository();
		if (permissionsTemplateDataSpaceName != null)
		{
			AdaptationHome templateDataSpace = repo.lookupHome(HomeKey.forBranchName(permissionsTemplateDataSpaceName));
			if (templateDataSpace == null)
			{
				LoggingCategory.getKernel().error(
					"Permissions template data space " + permissionsTemplateDataSpaceName
						+ " not found.");
			}
			else
			{
				homeCreationSpec.setHomeToCopyPermissionsFrom(templateDataSpace);
			}
		}
		return repo.createHome(homeCreationSpec, session);
	}

	protected void closeChildDataSpace(ServiceContext sContext, AdaptationHome childDataSpace)
		throws OperationException
	{
		childDataSpace.getRepository().closeHome(childDataSpace, sContext.getSession());
	}

	protected void processValidationReport(
		ServiceContext sContext,
		ValidationReport validationReport) throws IOException
	{
		if (validationReport.hasItemsOfSeverityOrMore(minSeverity))
		{
			List<ValidationErrorElement> list = getValidationErrorList(sContext, validationReport);
			generateUI(sContext, list);
		}
		else
		{

			sContext.getUIComponentWriter().add_cr("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No Validation Error");
		}
	}

	protected List<ValidationErrorElement> getValidationErrorList(
		ServiceContext sContext,
		ValidationReport validationReport)
	{
		final ValidationReportItemIterator itemsOfSeverity = validationReport.getItemsOfSeverityOrMore(minSeverity);

		final ArrayList<ValidationErrorElement> list = new ArrayList<ValidationErrorElement>();
		while (itemsOfSeverity.hasNext())
		{
			final ValidationReportItem nextItem = itemsOfSeverity.nextItem();
			if ((nextItem.getSubject() != null))
			{
				final ValidationErrorElement element = new ValidationErrorElement();
				if (nextItem.getSubjectForAdaptation() != null)
				{
					final Adaptation occurence = nextItem.getSubjectForAdaptation().getAdaptation();
					final UserMessage message = nextItem.getMessage();
					final String formatMessage = message.formatMessage(sContext.getLocale());
					element.setMessage(formatMessage + " ["
						+ nextItem.getSubjectForAdaptation().getPathInAdaptation().format() + "]");
					element.setRecord(occurence);
					list.add(element);
				}
				else if (nextItem.getSubjectForTable() != null)
				{
					Adaptation adaptation = null;
					if ((adaptation = nextItem.getSubjectForTable().getRecords().nextAdaptation()) != null)
					{
						final UserMessage message = nextItem.getMessage();
						final String formatMessage = message.formatMessage(sContext.getLocale());
						element.setMessage(formatMessage);
						element.setRecord(adaptation);
						list.add(element);
					}
				}
			}
		}
		return list;
	}

	protected void generateUI(ServiceContext sContext, List<ValidationErrorElement> list)
		throws IOException
	{
		// If it's null, then don't do the export at all
		if (exportParentDirName != null)
		{
			String filePath = generateFile(sContext, list);

			String downloadURL = sContext.getURLForResource(fileDownloaderServlet) + "?"
				+ FileDownloader.FILE_PATH_PARAM_NAME + "=" + filePath;
			downloadURL = downloadURL.replaceAll("\\\\", "/");

			sContext.getUIComponentWriter().add_cr(
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + downloadURL + "'>Download Report</a>");
		}
	}

	protected String generateFile(ServiceContext sContext, List<ValidationErrorElement> list)
		throws IOException
	{
		CSVExporter exporter = new CSVExporter(
			exportParentDirName,
			exportDirName,
			CSV_SEPARATOR,
			sContext.getLocale(),
			CSVExporter.DEFAULT_EXPORT_FILE_NAME,
			CSVExporter.DEFAULT_EXPORT_FILE_EXTENSION,
			appendTimestamp,
			includePK);
		exporter.setValidationList(list);
		return exporter.doExport();
	}

	public String getExportParentDirName()
	{
		return this.exportParentDirName;
	}

	public void setExportParentDirName(String exportParentDirName)
	{
		this.exportParentDirName = exportParentDirName;
	}

	public String getExportDirName()
	{
		return this.exportDirName;
	}

	public void setExportDirName(String exportDirName)
	{
		this.exportDirName = exportDirName;
	}

	public boolean isAppendTimestamp()
	{
		return this.appendTimestamp;
	}

	public void setAppendTimestamp(boolean appendTimestamp)
	{
		this.appendTimestamp = appendTimestamp;
	}

	public boolean isIncludePK()
	{
		return this.includePK;
	}

	public void setIncludePK(boolean includePK)
	{
		this.includePK = includePK;
	}

	public String getFileDownloaderServlet()
	{
		return this.fileDownloaderServlet;
	}

	public void setFileDownloaderServlet(String fileDownloaderServlet)
	{
		this.fileDownloaderServlet = fileDownloaderServlet;
	}

	public Severity getMinSeverity()
	{
		return this.minSeverity;
	}

	public void setMinSeverity(Severity minSeverity)
	{
		this.minSeverity = minSeverity;
	}

	public boolean isUseChildDataSpace()
	{
		return this.useChildDataSpace;
	}

	public void setUseChildDataSpace(boolean useChildDataSpace)
	{
		this.useChildDataSpace = useChildDataSpace;
	}

	public String getPermissionsTemplateDataSpaceName()
	{
		return this.permissionsTemplateDataSpaceName;
	}

	public void setPermissionsTemplateDataSpace(String permissionsTemplateDataSpaceName)
	{
		this.permissionsTemplateDataSpaceName = permissionsTemplateDataSpaceName;
	}
}
