package org.mgnl.nicki.editor.projects.members;

import org.mgnl.nicki.dynamic.objects.objects.Directory;
import org.mgnl.nicki.dynamic.objects.objects.Member;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class DirectoryComponent extends CustomComponent {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;
	@AutoGenerated
	private CheckBox checkBox_write;
	@AutoGenerated
	private CheckBox checkBox_read;
	@AutoGenerated
	private Label directoryLabel;
	private Directory directory;
	private Member member;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public DirectoryComponent(Member target, Directory dir) {
		this.member = target;
		this.directory = dir;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		directoryLabel.setValue(this.directory.getName());
		checkBox_read.setValue(member.hasReadRight(directory));
		checkBox_write.setValue(member.hasWriteRight(directory));
		
		/*
		checkBox_read.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				member.setReadRight(directory, checkBox_read.booleanValue());
				try {
					member.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		checkBox_write.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				member.setWriteRight(directory, checkBox_write.booleanValue());
				try {
					member.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		*/
	}

	public void save() {
		member.setReadRight(directory, checkBox_read.booleanValue());
		member.setWriteRight(directory, checkBox_write.booleanValue());
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("24px");
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout
				.addComponent(horizontalLayout_1,
						"top:0.0px;right:-2.9761963px;bottom:0.99993896px;left:10.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setWidth("100.0%");
		horizontalLayout_1.setHeight("24px");
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setMargin(false);
		
		// directoryLabel
		directoryLabel = new Label();
		directoryLabel.setWidth("200px");
		directoryLabel.setHeight("-1px");
		directoryLabel.setValue("Verzeichnis");
		directoryLabel.setImmediate(false);
		horizontalLayout_1.addComponent(directoryLabel);
		
		// checkBox_read
		checkBox_read = new CheckBox();
		checkBox_read.setWidth("-1px");
		checkBox_read.setHeight("-1px");
		checkBox_read.setCaption("read");
		checkBox_read.setImmediate(false);
		horizontalLayout_1.addComponent(checkBox_read);
		
		// checkBox_write
		checkBox_write = new CheckBox();
		checkBox_write.setWidth("-1px");
		checkBox_write.setHeight("-1px");
		checkBox_write.setCaption("write");
		checkBox_write.setImmediate(false);
		horizontalLayout_1.addComponent(checkBox_write);
		
		return horizontalLayout_1;
	}

}