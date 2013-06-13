package addressbook;

import javax.swing.JOptionPane;

public class Dialogs extends JOptionPane {

	public static int showQuestionDialog(Object message, String title) {

		return showConfirmDialog(null, message, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	}

	public static void showWaringDialog(Object message, String title) {

		showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}
}
