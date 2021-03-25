package com.vaadin.componentfactory.enhancedgrid.filtering;

import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.grid.FilterFieldDto;

public class TextFieldFilterDto implements FilterFieldDto<String> {

	private boolean wholeField;
	
	private boolean caseSensitive;
	
	private boolean regularExpression;
	
	private boolean invertResult;
	
	private String filterValue;
	
	public TextFieldFilterDto() {}
	
	public TextFieldFilterDto(String filterValue) {
		this.filterValue = filterValue;
	}
	
	public TextFieldFilterDto(boolean wholeField, boolean caseSensitive, boolean regularExpression,
			boolean invertResult, String filterValue) {
		this.wholeField = wholeField;
		this.caseSensitive = caseSensitive;
		this.regularExpression = regularExpression;
		this.invertResult = invertResult;
		this.filterValue = filterValue;
	}

	public boolean isWholeField() {
		return wholeField;
	}

	public void setWholeField(boolean wholeField) {
		this.wholeField = wholeField;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isRegularExpression() {
		return regularExpression;
	}

	public void setRegularExpression(boolean regularExpression) {
		this.regularExpression = regularExpression;
	}

	public boolean isInvertResult() {
		return invertResult;
	}

	public void setInvertResult(boolean invertResult) {
		this.invertResult = invertResult;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	
	@Override
	public Predicate<String> getFilterPredicate() {
		Predicate<String> simplePredicate = StringUtils.isNotBlank(filterValue) ? s -> s.contains(filterValue) : s -> true;
		if(regularExpression) {
			simplePredicate = s -> s.matches(filterValue);
			// TODO 			
		} else if(wholeField){			
			simplePredicate = caseSensitive ? s -> s.equals(filterValue) : s -> s.equalsIgnoreCase(filterValue);			
		} else if(caseSensitive) {
			simplePredicate = s -> s.equals(filterValue);
		} 
			
		return invertResult ? simplePredicate.negate() : simplePredicate;		
	}

	@Override
	public boolean isEmpty() {
		return StringUtils.isBlank(filterValue) && !invertResult && !regularExpression && !caseSensitive && !wholeField;
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseSensitive, filterValue, invertResult, regularExpression, wholeField);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TextFieldFilterDto))
			return false;
		TextFieldFilterDto other = (TextFieldFilterDto) obj;
		return caseSensitive == other.caseSensitive && Objects.equals(filterValue, other.filterValue)
				&& invertResult == other.invertResult && regularExpression == other.regularExpression
				&& wholeField == other.wholeField;
	}
	
}
