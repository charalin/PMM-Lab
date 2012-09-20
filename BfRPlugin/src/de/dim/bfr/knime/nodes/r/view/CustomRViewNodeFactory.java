/*******************************************************************************
 * Copyright (C) 2012 Data In Motion
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.dim.bfr.knime.nodes.r.view;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * Factory of the <code>RLocalViewsNodeModel</code>.
 *
 * @author Kilian Thiel, University of Konstanz
 */
public class CustomRViewNodeFactory extends
        NodeFactory<CustomRViewNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new CustomRViewNodeDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomRViewNodeModel createNodeModel() 
    {
        return new CustomRViewNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<CustomRViewNodeModel> createNodeView(
            final int viewIndex, final CustomRViewNodeModel nodeModel) {
    	
    	return new CustomRViewNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasDialog() {
        return true;
    }

}
