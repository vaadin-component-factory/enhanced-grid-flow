package com.vaadin.flow.component.grid;

/*-
 * #%L
 * Enhanced Grid
 * %%
 * Copyright (C) 2020 - 2021 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.SerializableRunnable;

@SuppressWarnings("serial")
public class CancelEditConfirmDialog<T> extends Dialog {
	
	private Paragraph message;
	
	private Button confirmButton;
	
	private Button cancelButton;
	
	public CancelEditConfirmDialog(String text, String confirmText, String cancelText, SerializableRunnable onConfirmCallback) {
	
		setCloseOnEsc(true);
    	setCloseOnOutsideClick(false);
    	    	
    	message = new Paragraph(text);
		
    	confirmButton = new Button(confirmText, e -> {
    		onConfirmCallback.run();
    		this.close();    		
    	});
    	
    	cancelButton = new Button(cancelText, e -> this.close());
    	
    	HorizontalLayout buttonsLayout = new HorizontalLayout();
    	buttonsLayout.setWidthFull();
    	buttonsLayout.setJustifyContentMode(JustifyContentMode.END); 
    	buttonsLayout.add(confirmButton, cancelButton);
    	add(message, buttonsLayout);
	}

	public Paragraph getMessage() {
		return message;
	}

	public void setMessage(Paragraph message) {
		this.message = message;
	}

	public Button getConfirmButton() {
		return confirmButton;
	}

	public void setConfirmButton(Button confirmButton) {
		this.confirmButton = confirmButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(Button cancelButton) {
		this.cancelButton = cancelButton;
	}	
		
}
