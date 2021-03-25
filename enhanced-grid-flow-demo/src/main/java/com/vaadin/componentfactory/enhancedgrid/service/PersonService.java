package com.vaadin.componentfactory.enhancedgrid.service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.componentfactory.enhancedgrid.bean.Person;
import com.vaadin.componentfactory.enhancedgrid.bean.PersonSort;
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

	public Stream<Person> fetchPersons(int offset, int limit, Optional<Filter<Person>> filter, List<PersonSort> sortOrders) {		
		List<Person> internalPersonsFullList = personData.getPersons();
		
		// apply sorting
		Comparator<Person> comparator = (o1, o2) -> 0;
		for (PersonSort personSort : sortOrders) {
			switch (personSort.getPropertyName()) {
				case PersonSort.FIRST_NAME:
					comparator = comparator.thenComparing(Person::getFirstName);
					break;
				case PersonSort.LAST_NAME:
					comparator = comparator.thenComparing(Person::getLastName);
					break;
				case PersonSort.AGE:
					comparator = comparator.thenComparing(Person::getAge);
					break;
			}
			if (!personSort.isDescending()) comparator = comparator.reversed();
		}
		List<Person> sortedList = new LinkedList<>(internalPersonsFullList);
		sortedList.sort(comparator);
		
		// get filter precdicate
		SerializablePredicate<Person> predicate = filter.map(Filter::getFilterPredicate).orElse(null);
		
		// apply filtering
		List<Person> result = sortedList.stream()
				.filter(person -> predicate != null ? predicate.test(person) : true)
				.skip(offset)
				.limit(limit).collect(Collectors.toList());
		return result.stream();		
	}

	public int getPersonCount(Optional<Filter<Person>> filter) {		
		List<Person> internalPersonsFullList = personData.getPersons();
		
		SerializablePredicate<Person> predicate = filter.map(Filter::getFilterPredicate).orElse(null);
		
		List<Person> result = internalPersonsFullList.stream()
				.filter(person -> predicate != null ? predicate.test(person) : true)
				.collect(Collectors.toList());
		return result.size();
	}

}