package addressbook;

import javax.swing.table.DefaultTableModel;

public class UnEditableTableModel extends DefaultTableModel {

	@Override
	public boolean isCellEditable(int row, int column) {
		// すべてのセルを直接編集不可にする
		return false;
	}


}
