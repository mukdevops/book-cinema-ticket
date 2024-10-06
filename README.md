# Cinema Ticket Booking Application

## Overview
This project implements a cinema ticket booking application using Java and Spring Boot. It allows customers to purchase various types of tickets (Infant, Child, Adult) while adhering to several business rules.

## Features
- Ability to purchase multiple types of tickets in one transaction.
- Tickets can be bought for **Adults**, **Children**, and **Infants**.
- **Infant** tickets are free and do not require a seat reservation.
- **Child** and **Infant** tickets cannot be purchased without an **Adult** ticket.
- A maximum of **25 tickets** can be purchased per transaction.
- Prices and limits are configurable through the `application.yml` file.

## Business Rules
- **Ticket Types**:
    - **Infant**: £0 (no seat reserved).
    - **Child**: £5 (seat reserved).
    - **Adult**: £15 (seat reserved).

- **Validation**:
    - At least one **Adult** ticket must be purchased.
    - No more than **25 tickets** can be purchased in a single transaction.

## Technologies Used
- **Java 17**
- **Spring Boot**
- **JUnit 5** for unit testing.
- **Mockito** for mocking dependencies.
- **Swagger** for API documentation.
- **JaCoCo** for code coverage reporting.

## Configuration

### `application.yml`
The application.yml file contains configuration values for ticket prices and maximum allowed tickets in a booking.

```yaml
ticketbooking:
  max-tickets: 25
