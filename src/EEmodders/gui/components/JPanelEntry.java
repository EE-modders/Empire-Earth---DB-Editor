package EEmodders.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.function.Supplier;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import EEmodders.constants.AttributeCode;
import EEmodders.constants.EffectCode;
import EEmodders.datmanager.DatFile;
import EEmodders.datmanager.Language;
import EEmodders.datstructure.Entry;
import EEmodders.datstructure.FieldStruct;
import EEmodders.datstructure.Link;
import EEmodders.gui.FrameEditor;
import EEmodders.gui.GUI;

/**
 * A panel which contains a label and a field
 *
 * @author MarcoForlini
 */
public class JPanelEntry extends JPanel {

	private static final long serialVersionUID = -8432430218424230659L;

	/** Index of the field */
	public final int index;
	/** Field structure */
	public final FieldStruct fieldStruct;
	/** The label */
	public JLabelField label;
	/** The field */
	public EntryFieldInterface field = null;
	/** A supplier which get the current entry */
	public Supplier<Entry> currentEntry;

	/**
	 * Create a new JPanelEntry
	 *
	 * @param frameEditor  The parent window
	 * @param fieldStruct  The field structure
	 * @param index        Index of the field
	 * @param currentEntry A supplier which get the current entry
	 */
	public JPanelEntry(FrameEditor frameEditor, FieldStruct fieldStruct, int index, Supplier<Entry> currentEntry) {
		this.index = index;
		this.fieldStruct = fieldStruct;
		this.currentEntry = currentEntry;

		label = new JLabelField(fieldStruct, index);
		label.setToolTipText(fieldStruct.getDescription());
		label.setPreferredSize(new Dimension(100, 25));
		label.setMaximumSize(new Dimension(300, 30));
		boolean disable = false;
		switch (fieldStruct.getType()) {
			case BOOLEAN:
				field = new JToggleBoxField(fieldStruct, index);
				break;
			case ENUM:
				if (fieldStruct.enumValues instanceof EffectCode[]) {
					field = new JComboBoxEnumEffect(fieldStruct, index, frameEditor);
				} else if (fieldStruct.enumValues instanceof AttributeCode[]) {
					field = new JComboBoxEnumAttribute(fieldStruct, index, frameEditor);
				} else {
					field = new JComboBoxEnum(fieldStruct, index);
				}
				break;
			case RANGE:
				field = new JComboBoxArray(fieldStruct, index);
				break;
			case LINK:
				if (fieldStruct.linkToStruct != null && fieldStruct.linkToStruct.datFile != null) {
					field = new JComboBoxField(fieldStruct, index);
				} else {
					disable = true;
				}
				break;
			case LANGUAGE:
				if (Language.getMap() != null && !Language.getMap().isEmpty()) {
					field = new JComboBoxLanguage(fieldStruct, index);
				}
				break;
			default:
				break;
		}
		if (field == null) {
			final JTextFieldField textField = new JTextFieldField(fieldStruct, index);
			if (fieldStruct.indexSize >= 0) {
				textField.registerUpdater(text -> frameEditor.setFieldValue(fieldStruct.indexSize, text.length()));
			}
			field = textField;
		}
		if (disable) {
			field.setForeground(GUI.COLOR_UI_ELEMENT);
			label.setForeground(GUI.COLOR_FIELD_ID_DISABLED);
			label.setToolTipText("File " + fieldStruct.linkToStruct.fileName + " not loaded");
		}
		field.setPreferredSize(new Dimension(100, 30));
		field.setMaximumSize(new Dimension(300, 45));
		setPreferredSize(new Dimension(100, 55));
		setMaximumSize(new Dimension(300, 75));
		setLayout(new GridLayout(2, 0, 0, 0));
		add(label);
		add((Component) field);
		setBorder(new EmptyBorder(4, 4, 4, 4));
		setOpaque(false);
		label.setOpaque(false);
	}

	/**
	 * Sets the value of the field
	 *
	 * @param val The value of the field
	 */
	public void setVal(Object val) {
		if (val instanceof Link) {
			field.setVal(((Link) val).target.getID());
		} else {
			field.setVal(val);
		}
	}

	/**
	 * Gets the value of the field
	 *
	 * @return the value of the field
	 */
	public Object getVal() {
		Object val;
		if (field.isAltered()) {
			val = field.getVal();
		} else {
			val = field.getDefaultVal();
		}
		if (fieldStruct.linkToStruct != null && fieldStruct.linkToStruct.datFile != null) {
			final DatFile datFile = fieldStruct.linkToStruct.datFile;
			final Entry target = datFile.findEntry(val);
			return new Link(currentEntry.get(), fieldStruct, target);
		}
		return val;
	}

	/**
	 * Refresh the field data
	 */
	public void refreshField() {
		field.refreshField();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		field.setEnabled(enabled);
	}

}
