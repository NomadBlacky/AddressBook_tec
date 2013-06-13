package addressbook;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;


public class AddressBook extends JFrame implements MouseListener {

	private JTable table;
	private DefaultTableModel model;
	/** 区切り文字(delimiter) */
	private String delm = ",";

	EditFrame editFrame;

	public AddressBook() {

		// コンポーネントの配置とリスナーの設定

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton btnLoad = new JButton("  Open  ");
		btnLoad.addActionListener(new OpenButtonAction());
		toolBar.add(btnLoad);

		JButton btnSave = new JButton("  Save  ");
		btnSave.addActionListener(new SaveButtonAction());
		toolBar.add(btnSave);

		JButton btnEdit = new JButton("  Edit  ");
		btnEdit.addActionListener(new ActionListener() {

			// 編集用ウィンドウを表示する
			public void actionPerformed(ActionEvent e) {

				if (!editFrame.isVisible()) {
					editFrame.setVisible(true);
				}
				else {
					editFrame.toFront();
				}
			}
		});

		toolBar.add(btnEdit);
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		model = new UnEditableTableModel();
		editFrame = new EditFrame(model);
		// editFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// ↑これを書くと呼び出し元のフレームまで閉じてしまう(プログラムが終了する)ので書かない

		table = new JTable(model);
		// 列の入れ替えを不可にする
		table.getTableHeader().setReorderingAllowed(false);
		// 列見出しをクリックでソートを有効にする
		table.setAutoCreateRowSorter(true);
		table.addMouseListener(this);

		scrollPane.setViewportView(table);

		// アドレス帳データを読み込む
		openFile(new File("./AddressBook.csv"));
	}

// "Open"ボタン押下時 --------------------------------------------------

	class OpenButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			// ファイル選択ダイアログを表示
			JFileChooser jChooser = new JFileChooser();
			jChooser.setCurrentDirectory(new File("."));
			int selected = jChooser.showOpenDialog(null);

			// 「開く」ボタン押下時
			if(selected == JFileChooser.APPROVE_OPTION) {

				// 選択したファイルを取得
				File file = jChooser.getSelectedFile();
				setTitle(String.format("%s - %s", file.getName(), file.getPath()));

				openFile(file);
			}
		}
	}

	private void openFile(File file) {

		try {
			// ファイルを読み込む
			Scanner scan = new Scanner(file);
			String[] colName = null;

			if(!scan.hasNext()) {
				// 空ファイルなら抜ける
				scan.close();
				return;
			}

			// 表をクリアする
			model.setRowCount(0);
			model.setColumnCount(0);

			// 先頭行を列見出しとして列を作成
			colName = scan.nextLine().split(delm);
			for (String s : colName) {
				model.addColumn(s);
			}

			// セルに値を設定する
			while (scan.hasNext()) {
				String[] line = scan.nextLine().split(delm);
				int nowCol = model.getColumnCount();
				if(line.length > nowCol) {
					// 列が足りなければ追加する
					for(int i = nowCol; i < line.length; i++) {
						model.addColumn(null);
					}
				}
				// データ一件追加
				model.addRow(line);
			}

			editFrame = new EditFrame(model);
			scan.close();

		} catch (FileNotFoundException e1) {
			Dialogs.showWaringDialog(file.getName().concat("が見つかりません。"), "エラー");
			setTitle("AddressBook");
		}

	}


// "Save"ボタン押下時 --------------------------------------------------

	class SaveButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			// ファイル選択ダイアログを表示
			JFileChooser jChooser = new JFileChooser();
			jChooser.setCurrentDirectory(new File("."));
			int selected = jChooser.showSaveDialog(null);

			// 「保存」ボタン押下時
			if(selected == JFileChooser.APPROVE_OPTION) {

				// 保存するファイル（パス）を取得
				File file = jChooser.getSelectedFile();

				// ファイルが存在する（上書き保存の）場合
				if(file.exists()) {

					// 確認ダイアログを表示
					int opt = Dialogs.showQuestionDialog(
							file.getName().concat("はすでに存在します。上書きしますか？"), "上書き保存");
					if(opt != Dialogs.OK_OPTION) {
						// OK が選択されなければ何もしない
						return;
					}
				}
				
				try {
					// テーブルの内容を書き込む
					FileWriter fw = new FileWriter(file);
					StringBuilder tableText = new StringBuilder();

					// 列名を書き込む
					for(int i = 0; i < model.getColumnCount(); i++) {

						tableText.append(model.getColumnName(i));

						// 最後の要素の後ろにカンマをつけない
						if (!(i >= model.getColumnCount() - 1)) {
							tableText.append(",");
						}
					}
					tableText.append("\n");

					// テーブル内容を書き込む
					for (int y = 0; y < model.getRowCount(); y++) {
						for (int x = 0; x < model.getColumnCount(); x++) {

							// セルの内容を取得
							// キャストなしでも問題なく動くため、あえてキャストしない。
							Object line = model.getValueAt(y, x);

							// 空のセルをnullと書き込まれるのを防ぐ
							if(line != null) {
								tableText.append(line);
							}
							// 最後の要素の後ろにカンマをつけない
							if (!(x >= model.getColumnCount() - 1)) {
								tableText.append(",");
							}
						}
						tableText.append("\n");
					}

					// テキストをまとめて書き込む
					fw.write(tableText.toString());
					fw.close();

				} catch (IOException e2) {
					Dialogs.showWaringDialog("保存に失敗しました。", "エラー");
				}

			}
		}
	}

// MouseListener -------------------------------------------------------

	@Override
	public void mouseClicked(MouseEvent e) {}

	// セルをクリックで選択したとき
	@Override
	public void mousePressed(MouseEvent e) {

		// 選択した行をEditFrameと同期する
		int row = table.getSelectedRow();
		editFrame.showData(table.convertRowIndexToModel(row));
		editFrame.toFront();

	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
