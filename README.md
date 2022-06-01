```
### ####### #     #    #     #####  ### #     #    #    ####### ### ####### #     # 
 #     #    ##   ##   # #   #     #  #  ##    #   # #      #     #  #     # ##    # 
 #     #    # # # #  #   #  #        #  # #   #  #   #     #     #  #     # # #   # 
 #     #    #  #  # #     # #  ####  #  #  #  # #     #    #     #  #     # #  #  # 
 #     #    #     # ####### #     #  #  #   # # #######    #     #  #     # #   # # 
 #     #    #     # #     # #     #  #  #    ## #     #    #     #  #     # #    ## 
###    #    #     # #     #  #####  ### #     # #     #    #    ### ####### #     #
```

[![Visit our website](https://img.shields.io/badge/Visit&nbsp;Our&nbsp;Website-ITMAGINATION-black.svg)](https://www.itmagination.com)

# ! VIDEO HERE

# Bank Contact Protection Application
Proof of Concept Android Application implementing concept described in *"Bank Contact Protection"* paper prepared by ITMAGINATION.

## Abstract
The security of modern information systems becomes more and more important. Especially systems that store sensitive medical and financial information. Although IT systems become more complicated, security is as strong as the weakest link in the security chain. Unfortunately in most cases the weakest links of system security are people who use it. 

Despite the growing IT systems, classic communication channels are still very popular. Mailing, SMS and phone calls are a part of client communication. Some companies are mixing classical and IT communications channels, making security more complex, multilayer and in the result vulnerable. A good example is the banking industry that very commonly uses both Web services, Mobile Applications, SMS and phone calls to communicate with customers.

This is a proof of concept of application improving vulnerabilities in customer contact services based on phone calls between two users. In such scenarios the greatest risk is posed by the insecure authentication procedure of the communication parties.

## This is a Proof Of Concept
This is an implementation of the Proof of Concept presented in the *"Bank Contact Protection"* paper prepared by ITMAGINATION. Please bear in mind that the purpose of the publishing this code is showcasing the concept presented in the paper, and not providing an application ready to use in production.

To make the application easier to understand, a few simplifications have been introduced:
1. Only basic authentication is implemented – not extra authentication procedures like two steps authentication was provided.
2. There is a one way communication from mobile application to backend server with REST API – no push notification used.