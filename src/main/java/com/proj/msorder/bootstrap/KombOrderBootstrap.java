package com.proj.msorder.bootstrap;

import com.proj.msorder.domain.Customer;
import com.proj.msorder.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Profile("test")
@Slf4j
@RequiredArgsConstructor
@Component
public class KombOrderBootstrap implements CommandLineRunner {


    public static final String KOMB_1_UPC = "0101234200036";
    public static final String KOMB_2_UPC = "0201234300019";
    public static final String KOMB_3_UPC = "0303783375213";
    public static final String TEST_CUSTOMER = "TestCustomer";

    private final CustomerRepository customerRepository;


    @Override
    public void run(String... args) throws Exception {
        loadSomeCustomers();
    }

    private void loadSomeCustomers() {
        List<Customer> testCustomerList = customerRepository.findAllByCustomerNameLike(KombOrderBootstrap.TEST_CUSTOMER);
        if (testCustomerList.size() == 0) {
            Customer savedCustomer = customerRepository.saveAndFlush(Customer.builder()
                    .customerName(TEST_CUSTOMER)
                    .apiKey(UUID.randomUUID())
                    .build());

            log.debug("Test Customer Id: " + savedCustomer.getId().toString());
        }else {
            //customerRepository.findAllByCustomerNameLike(KombOrderBootstrap.TEST_CUSTOMER)
            testCustomerList.forEach(customer -> log.debug("existing test customer id: "+customer.getId().toString()));
        }
    }
}
