package com.vaadin.componentfactory.enhancedgrid.filtering;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class TextFilterField extends AbstractCompositeField<Div, TextFilterField, TextFieldFilterDto> {

	private TextField filter;
	
	private VerticalLayout optionsLayout;
	
	private Checkbox wholeField;
	
	private Checkbox caseSensitive;
	
	private Checkbox regularExpression;
	
	private Checkbox invertResult;
	
	private Binder<TextFieldFilterDto> binder;
	
	public TextFilterField() {		
		super(new TextFieldFilterDto());
		init();
	}
	
	public TextFilterField(TextFieldFilterDto dto) {
		super(dto);
		init();
	}
	
	private void init() {
		binder = new Binder<>(TextFieldFilterDto.class);
		
		optionsLayout = new VerticalLayout();
		optionsLayout.setSpacing(false);
		optionsLayout.setPadding(false);
		
		wholeField = new Checkbox("Whole field");	
		binder.bind(wholeField, TextFieldFilterDto::isWholeField, TextFieldFilterDto::setWholeField);
		optionsLayout.add(wholeField);
		
		caseSensitive = new Checkbox("Case sensitive");
		binder.bind(caseSensitive, TextFieldFilterDto::isCaseSensitive, TextFieldFilterDto::setCaseSensitive);
		optionsLayout.add(caseSensitive);
		
		regularExpression = new Checkbox("Regular expresion");	
		binder.bind(regularExpression, TextFieldFilterDto::isRegularExpression, TextFieldFilterDto::setRegularExpression);
		optionsLayout.add(regularExpression);
		
		invertResult = new Checkbox("Invert filter result");
		binder.bind(invertResult, TextFieldFilterDto::isInvertResult, TextFieldFilterDto::setInvertResult);
		optionsLayout.add(invertResult);
		
		filter = new TextField();
		binder.bind(filter, TextFieldFilterDto::getFilterValue, TextFieldFilterDto::setFilterValue);
		
		binder.setBean(getValue());
		
		getContent().add(filter, optionsLayout);
	}

	@Override
	protected void setPresentationValue(TextFieldFilterDto textFieldFilterDto) {
		if(textFieldFilterDto == null) {
			this.clear();
		} else {
			filter.setValue(textFieldFilterDto.getFilterValue());
			wholeField.setValue(textFieldFilterDto.isWholeField());
			caseSensitive.setValue(textFieldFilterDto.isCaseSensitive());
			regularExpression.setValue(textFieldFilterDto.isRegularExpression());
			invertResult.setValue(textFieldFilterDto.isInvertResult());
		}		
	}
	
	@Override
	public void clear() {
		filter.clear();
		wholeField.clear();
		caseSensitive.clear();
		regularExpression.clear();
		invertResult.clear();
	}
		
	@Override
	public TextFieldFilterDto getEmptyValue() {
		return new TextFieldFilterDto(false, false, false, false, StringUtils.EMPTY);
	}
	
}
