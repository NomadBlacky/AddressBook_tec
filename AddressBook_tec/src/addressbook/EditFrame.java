package addressbook;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

public class EditFrame extends JFrame {

	/** AddressBookから渡されたモデル */
	private DefaultTableModel model;
	/**  */
	private JPanel mainPanel;

	/** 表示してから内容が変更されたか */
	boolean textEdited = false;

	public EditFrame(DefaultTableModel tmodel) {

		// DefaultTableModelのインスタンスを渡す
		// これにより、こちらで変更された内容がAddressBookにも反映される
		model = tmodel;

		// コンポーネントの配置とリスナーの設定

		this.setBounds(200, 200, 600, 400);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnSave = new JButton("保存(S)");
		btnSave.addActionListener(new SaveButtonAction());
		panel.add(btnSave);

		JButton btnPrev = new JButton("↑戻る(B)");
		btnPrev.addActionListener(new PrevButtonAction());
		panel.add(btnPrev);

		JButton btnNext = new JButton("次へ↓(N)");
		btnNext.addActionListener(new NextButtonAction());
		panel.add(btnNext);

		JButton btnNew = new JButton("新規(E)");
		btnNew.addActionListener(new NewButtonAction());
		panel.add(btnNew);

		JButton btnDelete = new JButton("削除(D)");
		btnDelete.addActionListener(new DeleteButtonAction());
		panel.add(btnDelete);

		JButton btnExit = new JButton("終了(X)");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		panel.add(btnExit);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// GridBagLayoutの設定（よくわからない）
		mainPanel = new JPanel();
		mainPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane.setViewportView(mainPanel);
		GridBagLayout gbl_panelMain = new GridBagLayout();
		gbl_panelMain.columnWidths = new int[]{0, 0, 0};
		gbl_panelMain.rowHeights = new int[] {30, 0};
		gbl_panelMain.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelMain.rowWeights = new double[]{0.0, 0.0};
		mainPanel.setLayout(gbl_panelMain);

		// 余白用のパネル
		JPanel hogePanel = new JPanel();
		hogePanel.setBorder(null);
		getContentPane().add(hogePanel, BorderLayout.NORTH);

		// 初期化処理
		init();

	}

// ActionListener ---------------------------------------------
	
	class SaveButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			saveData();
			textEdited = false;
		}
		
	}

	// 「戻る」ボタン
	class PrevButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			prevData();
			textEdited = false;
		}
	}

	// 「次へ」ボタン
	class NextButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			nextData();
			textEdited = false;
		}
	}

	// 「新規登録」ボタン
	class NewButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			newData();
			textEdited = false;
		}
	}

	// 「削除」ボタン
	class DeleteButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			deleteData();
			textEdited = false;
		}
	}

// KeyListener ---------------------------------------------

	class TextEditing implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			textEdited = true;
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}

	}

// ---------------------------------------------------------

	/** テキストフィールドたち */
	private ArrayList<JTextField> textFields = new ArrayList<>();

	/** 現在表示している行番号 */
	private int nowDataRow = 0;

	/** JLabelとJTextFieldの初期化 */
	private void init() {

		for(int i = 0; i < model.getColumnCount(); i++) {

			// 列の分だけ動的にコンポーネントを生成
			// GridBadLayoutはよくわかってないので説明は割愛

			JLabel label = new JLabel(model.getColumnName(i));
			GridBagConstraints labelGbc = new GridBagConstraints();
			labelGbc.ipady = 2;
			labelGbc.ipadx = 2;
			labelGbc.insets = new Insets(5, 5, 5, 5);
			labelGbc.anchor = GridBagConstraints.EAST;
			labelGbc.gridx = 0;
			labelGbc.gridy = i;
			mainPanel.add(label, labelGbc);

			JTextField textField = new JTextField((String)model.getValueAt(nowDataRow, i));
			// テキストが編集時のリスナーを追加
			textField.addKeyListener(new TextEditing());
			textFields.add(textField);
			GridBagConstraints textGbc = new GridBagConstraints();
			textGbc.ipady = 2;
			textGbc.ipadx = 2;
			textGbc.insets = new Insets(5, 5, 5, 5);
			textGbc.fill = GridBagConstraints.HORIZONTAL;
			textGbc.gridx = 1;
			textGbc.gridy = i;
			mainPanel.add(textField, textGbc);
			textField.setColumns(10);
		}

	}

	/** 編集されたデータを更新したのち、指定した行のデータを表示する。 */
	private boolean update(int showRow) {

		if(showRow < 0 || showRow >= model.getRowCount()) {
			// 範囲外なら抜ける
			return false;
		}

		// 編集したデータをテーブルに更新
		for(int i = 0; i < model.getColumnCount(); i++) {
			String text = textFields.get(i).getText();
			model.setValueAt(text, nowDataRow, i);
		}

		// テキストフィールドに新しくデータを表示
		for(int i = 0; i < model.getColumnCount(); i++) {
			String text = (String)model.getValueAt(showRow, i);
			textFields.get(i).setText(text);
		}

		// 表示が完了したら現在のインデックスを更新
		nowDataRow = showRow;

		return true;
	}

	/** 指定した行のデータを表示する */
	public boolean showData(int showRow) {

		if(showRow < 0 || showRow >= model.getRowCount()) {
			// 範囲外なら抜ける
			return false;
		}

		// テキストフィールドに新しくデータを表示
		for(int i = 0; i < model.getColumnCount(); i++) {
			String text = (String)model.getValueAt(showRow, i);
			textFields.get(i).setText(text);
		}

		// 現在のインデックスを更新
		nowDataRow = showRow;
		return true;
	}

	/** 新しく行を追加する */
	private void newData() {

		// 列数分のセルをもつ行を追加し、表示する
		Object[] objects = new Object[model.getColumnCount()];
		model.addRow(objects);
		showData(model.getRowCount() - 1);

		// テキストフィールドを編集可能にする
		for (JTextField text : textFields) {
			text.setEditable(true);
		}

	}

	private void saveData() {

		// 編集したデータをテーブルに更新
		for(int i = 0; i < model.getColumnCount(); i++) {
			String text = textFields.get(i).getText();
			model.setValueAt(text, nowDataRow, i);
		}

	}

	/** 編集中の行を削除する */
	private void deleteData() {

		if(model.getRowCount() == 0) {
			// 削除するものがなければ抜ける
			return;
		}

		// 警告ダイアログ
		int opt = Dialogs.showQuestionDialog("本当に削除しますか？", "");
		if(opt != Dialogs.OK_OPTION) {
			// OK が選択されなければ何もしない
			return;
		}

		// 現在行を削除
		model.removeRow(nowDataRow);

		// 削除された行の次にあたる行を表示する
		// 最終行を削除した場合、IndexOutOfBoundsするので、
		// 右辺の(nowDataRow - 1)を実行する。
		// &&演算子は、左辺がfalseだった場合、右辺は実行されない。

		if(!showData(nowDataRow) && !showData(nowDataRow - 1)) {

			// 削除したのちテーブルが空なら、テキストフィールドを編集不可にする。
			for (JTextField text : textFields) {
				text.setEditable(false);
				text.setText("");
			}
		}



	}

	private boolean prevData() {

		if(textEdited && nowDataRow != 0) {
			int opt = Dialogs.showQuestionDialog("編集中の内容を破棄してもよろしいですか？", "");
			if(opt != Dialogs.OK_OPTION) {
				return false;
			}
		}
		return showData(nowDataRow - 1);

	}

	private boolean nextData() {

		if(textEdited && nowDataRow != model.getRowCount() - 1) {
			int opt = Dialogs.showQuestionDialog("編集中の内容を破棄してもよろしいですか？", "");
			if(opt != Dialogs.OK_OPTION) {
				return false;
			}

		}
		return showData(nowDataRow + 1);
	}


}