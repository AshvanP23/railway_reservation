Railway Reservation System (SQL Backend)

A robust and scalable Relational Database Management System (RDBMS) designed to handle the complexities of railway operations, including scheduling, station routing, and passenger bookings.

Project Overview :

This project focuses on the core backend architecture of a railway system. It manages everything from train routes (like Kovai Express, Shatabdi) to passenger seat allocations and ticket fares across major stations like Chennai Central (MAS), Coimbatore (CBE), and Madurai (MDU).

Database Architecture (Schema) :

The system is built on a highly normalized SQL structure consisting of:

Stations Table: Mapping station codes (e.g., AJJ, KPD) to their full names.

Trains Table: Master list of trains with numbers, names, durations, and running days.

Train_Routes Table: Detailed stop-by-stop mapping with arrival/departure timings and intermediate ticket pricing.

Users Table: Secure storage for user credentials and contact information.

Bookings Table: Handles ticket generation, PNR mapping, and fare calculations.

Passengers Table: Relational mapping of multiple passengers to a single Booking ID.

 Key Features :

Route Optimization: Stop-order logic to calculate precise arrival and departure sequences.

Dynamic Pricing: Station-to-station ticket fare calculation based on the route mapping.

PNR Logic: Unique PNR generation for every successful booking.

Data Integrity: Strictly enforced Foreign Key constraints to prevent orphaned booking or passenger data.

High Performance: Indexed queries for fast train searches between specific source and destination codes.

Tech Stack :

Database: MySQL / MariaDB

Scripting: Structured Query Language (SQL)

Concepts: ACID Properties, Data Normalization, Relational Mapping

SQL Implementation Details :

1. Train Scheduling

The system tracks real-time durations (e.g., Kovai Express - 7H 35M) and manages weekly schedules using a comma-separated string format for running days.

2. Route Mapping

Unlike basic systems, this DB handles Intermediate Stops. If a train goes from MAS to CBE, the system knows exactly when it reaches Arakkonam (AJJ) or Katpadi (KPD).

3. Booking Flow

User selects a train and stations.

System calculates total fare.

Passenger details are linked to a single booking_id.

PNR is marked as CONFIRMED by default.
