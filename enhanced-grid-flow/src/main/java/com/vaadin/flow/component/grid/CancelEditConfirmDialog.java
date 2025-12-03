package com.vaadin.flow.component.grid;

/*-
 * #%L
 * Enhanced Grid
 * %%
 * Copyright (C) 2020 - 2025 Vaadin Ltd
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

/**
 * Dialog to confirm canceling an edit operation.
 */
@SuppressWarnings("serial")
public class CancelEditConfirmDialog extends Dialog {

	private Paragraph message;

	private Button confirmButton;

	private Button cancelButton;

	/**
	 * Constructor with message, confirm text, cancel text, confirm callback and
	 * cancel callback.
	 * 
	 * @param text              the message to display
	 * @param confirmText       the text for the confirm button
	 * @param cancelText        the text for the cancel button
	 * @param onConfirmCallback the callback to run when the confirm button is
	 *                          clicked
	 * @param onCancelCallback  the callback to run when the cancel button is
	 *                          clicked
	 */
	public CancelEditConfirmDialog(String text, String confirmText, String cancelText,
			SerializableRunnable onConfirmCallback,
			SerializableRunnable onCancelCallback) {

		setCloseOnEsc(true);
		setCloseOnOutsideClick(false);

		message = new Paragraph(text);

		confirmButton = new Button(confirmText, e -> {
			onConfirmCallback.run();
			this.close();
		});

		cancelButton = new Button(cancelText, e -> {
			if (onCancelCallback != null) {
				onCancelCallback.run();
			}
			this.close();
		});

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidthFull();
		buttonsLayout.setJustifyContentMode(JustifyContentMode.END);
		buttonsLayout.add(confirmButton, cancelButton);
		add(message, buttonsLayout);
	}

	/**
	 * Get the message.
	 * 
	 * @return the message
	 */
	public Paragraph getMessage() {
		return message;
	}

	/**
	 * Get the confirm button.
	 * 
	 * @return the confirm button
	 */
	public Button getConfirmButton() {
		return confirmButton;
	}

	/**
	 * Get the cancel button.
	 * 
	 * @return the cancel button
	 */
	public Button getCancelButton() {
		return cancelButton;
	}

}
