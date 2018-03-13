/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.uibeaneditor;

import java.util.*;

import com.onwbp.base.misc.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.constants.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;
import com.orchestranetworks.ui.form.widget.*;

/**
 * Shows the enumeration values for a node, allowing certain values to be excluded.
 * This can be useful rather than a <code>ConstraintEnumeration</code>, because that will not
 * limit the choices in a search to only the values returned. Using a built-in enumeration and
 * then limiting what the user sees in the drop-down allows you to search on the enumeration values
 * but also limit the user. Can be used in conjunction with a <code>Constraint</code> or
 * <code>ConstraintOnTableWithRecordLevelCheck</code> if you want to make sure the data gets validated after import
 * or after other values it depends on get changed.
 */
public abstract class RestrictedEnumerationUIBeanEditor extends UIBeanEditor
{
	protected boolean alwaysAllowCurrentValue;

	private boolean alwaysEnforceRestriction;

	protected RestrictedEnumerationUIBeanEditor()
	{
		this(true);
	}

	protected RestrictedEnumerationUIBeanEditor(boolean alwaysAllowCurrentValue)
	{
		this.alwaysAllowCurrentValue = alwaysAllowCurrentValue;
	}

	@Override
	public void addForDisplay(UIResponseContext context)
	{
		doAddForDisplay(context);
	}

	@Override
	public void addForEdit(UIResponseContext context)
	{
		Session session = context.getSession();
		if (ignoreRestriction(session))
		{
			context.addWidget(Path.SELF);
		}
		else
		{
			UIComboBox comboBox = context.newComboBox(Path.SELF);
			ValueContext vc = context.getValueContext();
			// TODO: Should we set prevalidation enabled to true?
			//comboBox.setAjaxPrevalidationEnabled(true);
			comboBox.setSpecificNomenclature(createNomenclature(vc, session));
			context.addWidget(comboBox);
		}
	}

	@Override
	public void addForDisplayInTable(UIResponseContext context)
	{
		doAddForDisplay(context);
	}

	protected boolean ignoreRestriction(Session session)
	{
		return !alwaysEnforceRestriction && session.isUserInRole(CommonConstants.TECH_ADMIN);
	}

	protected abstract boolean allowValue(Object value, ValueContext context, Session session);

	protected Nomenclature createNomenclature(ValueContext context, Session session)
	{
		Nomenclature nomenclature = new Nomenclature();
		SchemaNode node = context.getNode();
		Object currValue = context.getValue();
		@SuppressWarnings("unchecked")
		List<Object> enumValues = node.getEnumerationList(context);
		for (Object enumValue : enumValues)
		{
			if ((alwaysAllowCurrentValue && ObjectUtils.equals(enumValue, currValue))
				|| allowValue(enumValue, context, session))
			{
				nomenclature.addItemValue(
					enumValue,
					node.displayOccurrence(enumValue, true, context, session.getLocale()));
			}
		}
		return nomenclature;
	}

	private static void doAddForDisplay(UIResponseContext context)
	{
		UIWidget widget = context.newBestMatching(Path.SELF);
		widget.setEditorDisabled(true);
		context.addWidget(widget);
	}

	public boolean isAlwaysEnforceRestriction()
	{
		return this.alwaysEnforceRestriction;
	}

	public void setAlwaysEnforceRestriction(boolean alwaysEnforceRestriction)
	{
		this.alwaysEnforceRestriction = alwaysEnforceRestriction;
	}
}
