package EEmodders.gui;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import EEmodders.datmanager.DatFile;
import EEmodders.datstructure.Entry;
import EEmodders.datstructure.EntryGroup;
import EEmodders.gui.components.JButtonRed;
import EEmodders.gui.components.JListEntry;
import EEmodders.gui.components.JSearchTextField;
import EEmodders.gui.misc.EEScrollBarUI;
import EEmodders.gui.misc.GridBagConstraintsExtended;
import EEmodders.gui.misc.GridBagLayoutExtended;

/**
 * In the {@code DialogSearchValuesResults} dialog the user can double click on any entry to get the full list of
 * entries.
 * This dialog show this list of entries.
 *
 * @author MarcoForlini
 */
public class DialogSearchValuesResultsList extends JDialog {

	private static final long serialVersionUID = 7589015334494498605L;

	/**
	 * Create a new {@link DialogSearchValuesResultsList}
	 *
	 * @param parent The parent window
	 * @param list   The list of entries
	 * @param value  The selected value
	 */
	public DialogSearchValuesResultsList(Window parent, DatFile datFile, List<Entry> list, Object value) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		final JLabel dlgLabel = new JLabel("All entries with this value:");
		final JListEntry dlgList = new JListEntry(list);
		final JScrollPane dlgScrollPane = new JScrollPane(dlgList);
		final JSearchTextField<Entry> dlgSearch = new JSearchTextField<>(dlgList, Entry::filterGenerator);
		final JButton dlgClose = new JButtonRed("Close");

		getContentPane().setBackground(GUI.COLOR_UI_BACKGROUND);
		dlgLabel.setOpaque(false);
		dlgScrollPane.setOpaque(false);
		dlgScrollPane.getViewport().setOpaque(false);
		dlgScrollPane.getVerticalScrollBar().setUI(new EEScrollBarUI());
		dlgScrollPane.getHorizontalScrollBar().setUI(new EEScrollBarUI());

		getRootPane().registerKeyboardAction((e) -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		dlgClose.addActionListener(e2 -> dispose());

		setTitle("For value: " + value);
		setBounds(GUI.getBounds(this, 0.45, 0.6));
		setLayout(new GridBagLayoutExtended(new int[] { 200 }, new int[] { 30, 400, 25, 30, 50 }, new double[] { 1.0 }, new double[] { 0, 1.0, 0, 0, 0 }));

		final JPanel entryListOptionsPanel = new JPanel();
		entryListOptionsPanel.setLayout(new GridLayout(1, 2));
		if (datFile.datStructure.indexLanguage >= 0) {
			entryListOptionsPanel.add(dlgList.localizeToggle);
		}
		entryListOptionsPanel.add(dlgList.filterToggle);
		dlgList.filterToggle.setHorizontalAlignment(SwingConstants.RIGHT);
		dlgList.filterToggle.setHorizontalTextPosition(SwingConstants.RIGHT);

		add(dlgLabel, new GridBagConstraintsExtended(5, 5, 0, 5, 0, 0));
		add(dlgScrollPane, new GridBagConstraintsExtended(5, 5, 0, 5, 0, 1));
		add(entryListOptionsPanel, new GridBagConstraintsExtended(5, 5, 0, 5, 0, 2));
		add(dlgSearch, new GridBagConstraintsExtended(5, 5, 0, 5, 0, 3));
		add(dlgClose, new GridBagConstraintsExtended(5, 5, 5, 5, 0, 4));

		dlgList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final int index = dlgList.getSelectedIndex();
				if (e.getClickCount() == 2) {
					final Entry selEntry = dlgList.get(index);
					if (selEntry != null) {
						final DatFile datFile = selEntry.datStructure.datFile;
						if (datFile != null) {
							final EntryGroup entryGroup = datFile.findGroup(selEntry);
							if (entryGroup != null) {
								final FrameEditor frameEditor = datFile.openInEditor(DialogSearchValuesResultsList.this, true);
								frameEditor.goToEntry(entryGroup, selEntry);
							}
						}
					}
				}
			}
		});

		dlgSearch.addSearchListener(text -> {
			dlgList.filterToggle.setEnabled(text == null);
		});
	}

}
