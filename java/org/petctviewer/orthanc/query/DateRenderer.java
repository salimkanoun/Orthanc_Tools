/**
Copyright (C) 2017 VONGSALAT Anousone

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public v.3 License as published by
the Free Software Foundation;

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.petctviewer.orthanc.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableCellRenderer;


public class DateRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;
	DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

	public DateRenderer() {
		super();
	}      

	public void setValue(Object value) {
		setText((value == null) ? "" : formatter.format(value));
	}
}