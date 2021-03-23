package com.vaadin.componentfactory.enhancedgrid.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.componentfactory.enhancedgrid.bean.Person;
import com.vaadin.componentfactory.enhancedgrid.data.PersonData;
import com.vaadin.flow.component.grid.Filter;
import com.vaadin.flow.function.SerializablePredicate;

public class PersonService {
	
    private PersonData personData = new PersonData();

    public List<Person> fetch(int offset, int limit) {
        int end = offset + limit;
        int size = personData.getPersons().size();
        if (size <= end) {
            end = size;
        }
        return personData.getPersons().subList(offset, end);
    }

    public int count() {
        return personData.getPersons().size();
    }

    public List<Person> fetchAll() {
        return personData.getPersons();
    }

	public Stream<Person> fetchPersons(int offset, int limit, Optional<Filter> filter) {		
		List<Person> internalPersonsFullList = personData.getPersons();
		
		SerializablePredicate predicate = filter.map(Filter::getFilterPredicate).orElse(null);
		
		List<Person> result = internalPersonsFullList.stream()
				.filter(person -> predicate != null ? predicate.test(person) : true)
				.skip(offset)
				.limit(limit).collect(Collectors.toList());
		return result.stream();
		
	}

	public int getPersonCount(Optional<Filter> filter) {		
		List<Person> internalPersonsFullList = personData.getPersons();
		
		SerializablePredicate predicate = filter.map(Filter::getFilterPredicate).orElse(null);
		
		List<Person> result = internalPersonsFullList.stream()
				.filter(person -> predicate != null ? predicate.test(person) : true)
				.collect(Collectors.toList());
		return result.size();
	}
}