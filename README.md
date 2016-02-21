# UssdSessionManager

UssdSessionManager is a free open source USSD session management system.

##HELP

This project using state machine for managing ussd session. You can see one example
ussd state machin in src/main/resources/states.xml.

This state machine engine using 4 type of state for managing USSD session:
###INPUT:
This state is used for creating USSD menu with input box.
####menu
Used for showing the menu in ussd, you can use variable in the text of menu with 
this format -> "!variable!", this text is replaced by value of this variable.
####variable
When user enter a text after viewing this menu, this text is saved to this variable
name for using in future states.
####next_state
After current state the engine run this state.
####next_state_variable
This tag used when you want to create a conditional next state.
Value of this tag is the variable name that condition is done based on that.
example:
    <next_state_variable>var1</next_state_variable>
    <var_1>GetCity</var_1> <!-- var_(value) -->
    <var_2>ExitState</var_2>
    <var_else>ErrorState</var_else>
means:
if (var1==1)
    goto GetCity
else if (var1==2)
    goto ExitState
else 
    goto ErrorState

###SERVICE:
This state is used for calling a rest service in the chain of ussd session.
####url
URL of rest service, you can use variable in the text of url with this format -> "!variable!", 
this text is replaced by value of this variable.
####connection_timeout
Set connection timeout.
####socket_timeout
Set socket timeout.
####next_state
Like INPUT type
####next_state_variable
Like INPUT type
###FORWARD:
This state is used for forwarding from this short code to onother USSD short code.
####ussd
Short code for forwarding to.
###END:
This state is used for creating USSD menu without input box.
####menu
Like INPUT type.

## Installation

The installation of this project is so easy 

1. Download project
2. Open it on netbeans
3. Run project



## License

[License](LICENSE)