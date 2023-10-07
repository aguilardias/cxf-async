
# Generate java classes from wsdl
`mvn clean package -DskipTests`

# Run Unit test to make webservice call
- TestCxfIT.numberConversion, works

- TestCxfIT.sabreAsyncFail, receive: `jakarta.xml.ws.soap.SOAPFaultException: Unable to internalize message`
- TestCxfIT.sabreSyncSuccess

## in order to see decrypt data, use:
https://github.com/neykov/extract-tls-secrets  
run unit test (Eclipse ide), Arguments>VM arguments  
`-ea -javaagent:~/Downloads/extract-tls-secrets-4.0.0.jar=/tmp/secrets.log`

# Use Wireshark to inspect network communication
https://www.youtube.com/watch?v=5qecyZHL-GU

# See data folder with test results