package addressbook;

import javax.swing.JOptionPane;

public class Dialogs extends JOptionPane {

	/** 二択のダイアログ */
	public static int showOkCancelDialog(Object message, String title) {

		return showConfirmDialog(null, message, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	}

	/** 三択のダイアログ */
	public static int showYNC_Dialog(Object message, String title) {

		return showConfirmDialog(null, message, title,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	/** OKのみのダイアログ */
	public static void showInfoDialog(Object message, String title) {

		showMessageDialog(null, message, title, INFORMATION_MESSAGE);
	}

	/** 警告のダイアログ */
	public static void showWaringDialog(Object message, String title) {

		showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}
}
