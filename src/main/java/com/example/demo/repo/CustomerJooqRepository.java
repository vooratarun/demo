//package com.example.demo.repo;
//
//import com.example.jooq.generated.tables.Customer;
//import com.example.jooq.generated.tables.records.CustomerRecord;
//import org.jooq.DSLContext;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import static com.example.jooq.generated.tables.Customer.CUSTOMER;
//
//@Repository
//public class CustomerJooqRepository {
//
//    private final DSLContext dsl;
//
//    public CustomerJooqRepository(DSLContext dsl) {
//        this.dsl = dsl;
//    }
//
//    // Fetch all customers
//    public List<com.example.jooq.generated.tables.pojos.Customer> findAll() {
//        return dsl.selectFrom(CUSTOMER)
//                  .fetchInto(com.example.jooq.generated.tables.pojos.Customer.class);
//    }
//
//    // Find by id
//    public Optional<com.example.jooq.generated.tables.pojos.Customer> findById(Long id) {
//        CustomerRecord rec = dsl.selectFrom(CUSTOMER)
//                                .where(CUSTOMER.ID.eq(id))
//                                .fetchOne();
//        return rec == null ? Optional.empty() : Optional.of(rec.into(com.example.jooq.generated.tables.pojos.Customer.class));
//    }
//
//    // Insert - returns generated id
//    public Long insert(String name, String email, String phone) {
//        CustomerRecord rec = dsl.insertInto(CUSTOMER)
//            .set(CUSTOMER.NAME, name)
//            .set(CUSTOMER.EMAIL, email)
//            .set(CUSTOMER.PHONE_NUMBER, phone)
//            .returning(CUSTOMER.ID)
//            .fetchOne();
//        return rec.getId();
//    }
//
//    // Update
//    public int updatePhone(Long id, String phone) {
//        return dsl.update(CUSTOMER)
//                  .set(CUSTOMER.PHONE_NUMBER, phone)
//                  .where(CUSTOMER.ID.eq(id))
//                  .execute();
//    }
//
//    // Delete
//    public int deleteById(Long id) {
//        return dsl.deleteFrom(CUSTOMER)
//                  .where(CUSTOMER.ID.eq(id))
//                  .execute();
//    }
//
//    // Aggregate example: count customers by city (if customer had city field)
//    // public List<Record2<String, Integer>> countByCity() { ... }
//}
