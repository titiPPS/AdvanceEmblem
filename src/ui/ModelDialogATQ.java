package ui;

import javax.swing.table.AbstractTableModel;


public class ModelDialogATQ extends AbstractTableModel {
	public String[] columnNames;
	public Object[][] datas;
	
	
	public ModelDialogATQ(int length,String[] columnNames) {
		datas = new Object[columnNames.length][length];
		this.columnNames = columnNames.clone();
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return datas[0].length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return datas[columnIndex][rowIndex];
	}

	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

}
