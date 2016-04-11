## Generating a self-signed certificate

1. Use **keytool** (included with Java insallations in `%JAVA_HOME%\bin`) to generate a certificate in a keystore.
  1. Open a command prompt/shell (on Windows be sure to open Command Prompt as an Administrator) and set the current directory to `%JAVA_HOME%\bin`.
  1. Run the following command to generate a new certificate in a keystore. Answer the prompts to create the certificate.
    
    ```Shell
    keytool -genkey -keyalg RSA -alias calendardemo -keystore calendardemo.jks -storepass MySecurePassword -validity 360 -keysize 2048
    ```
    > **NOTE**: Replace `MySecurePassword` with a secure password of your choice. You can also change the `-validity` parameter to be shorter or longer if you wish. The `-keyalg` and `-keysize` parameters must be set as in the above example.
    
  1. Export the public key to a file using the following command.
  
    ```Shell
    keytool -export -alias calendardemo -keystore calendardemo.jks -file calendardemo.cer
    ```