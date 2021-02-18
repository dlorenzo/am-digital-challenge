# The Asset Management Digital Challenge

Simple REST API that provides minimal bank account management functionality: create, get account info and 
transfer money between accounts.

## TODO

Some improvements this small APP should implement:

* Detach domain objects from the outside world (use DTO on controllers, domain on services and repositories).
* Implement proper error handling (Problems, exception handlers and translators, ControllerAdvice, etc.) and
  cleaner validation error messages.
* Transactions should be decoupled from Account: its own service and repository. Storing each transaction
would also be a good idea as it adds necessary traceability.
* Obviously, in-memory databases should not be used in production environments. With a proper database and abstraction
  layer, transactionality could be added. Spring Data JPA.
* Adding Swagger API documentation via annotations (for example, Springfox).
* Due to the nature of this API and its operations: every endpoint should be secured, for example, with 
Spring Security and the use of JSON Web Tokens.
* Make sure that balance and transfer amounts never exceed 2 decimal places.
