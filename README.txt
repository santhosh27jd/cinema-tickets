#READ ME#
# TICKET SERVICE #

#--- JAVA CLASS ---#

# BEAN #
 - TicketTypeRequest - Contains request details from user
 - TicketDetails - Stores ticket details for the account

# THIRD PARTY API #
 - TicketPaymentService - Assumption, Payment gateway to process payment
 - SeatReservationService - Assumption, Seat reservation service

# STORAGE #
 - It is an in memory storage used Hashmap.
 - CacheManager - Stores purchased ticket information for an account. Uses mostly for unit testing.

# TEST #
 - TicketServiceTest - Test class for running unit test cases

# MAIN #
 - TicketService - Main service has business logic


#--- BUILD TOOL ---#
 - Maven



