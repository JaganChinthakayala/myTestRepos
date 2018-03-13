package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.ui.form;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPane;
import com.orchestranetworks.ui.form.UIFormPaneWriter;

/**
 * Define a UIFormPane with an association table.
 *
 * @author ATI
 * @since 1.0.0
 */
public final class UIFormAssociationPane implements UIFormPane
{
	private final Path path;

	/**
	 * @param path the path of the association node.
	 * @since 1.0.0
	 */
	public UIFormAssociationPane(final Path path)
	{
		super();
		this.path = path;
	}

	/**
	 * @param node the node of the association.
	 * @since 1.0.0
	 */
	public UIFormAssociationPane(final SchemaNode node)
	{
		super();
		if (node != null)
		{
			this.path = Path.SELF.add(node.getPathInAdaptation());
		}
		else
		{
			this.path = null;
		}
	}

	@Override
	public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext)
	{
		if (this.path != null)
		{
			pWriter.addWidget(this.path);
		}
	}
}
