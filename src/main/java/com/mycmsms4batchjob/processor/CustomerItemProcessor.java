package com.mycmsms4batchjob.processor;

import com.mycmsms4batchjob.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Read input object as customer,
 * Write object as customer
 */
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerItemProcessor.class);

    /**
     * Used to filter out the information while processing, read, write the data
     * @param customer object
     * @return customer
     */
    @Override
    public Customer process(Customer customer) {

        // This will check the csv, only record with country = france will get processed
//        if (customer.getCountry().equals("France")) {
//            return customer;
//        } else {
//            return null;
//        }
        return customer;
    }
}
