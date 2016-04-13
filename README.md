# Java Calendar Demo

This sample Java Web App implements the [client credential](https://msdn.microsoft.com/en-us/library/azure/dn645543.aspx) authorization flow to gain access to all calendar's in an Office 365 organization via the [Microsoft Graph](http://graph.microsoft.io).

## Prerequisites

This project was created using the [Eclipse JEE Mars IDE](http://www.eclipse.org/), Mars.2 Release (4.5.2), using Tomcat 7.0 as the web server.

## Setting up the project

In order to get access tokens for the Microsoft Graph with access to all calendars, the app must use a certficate to sign token requests. So the first order of business is to generate the necessary certificate and get the required information.

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
    
  1. Copy the `calendardemo.jks` file to `.\src\main\webapp\WEB-INF`. Copy the calendardemo.cer file to `.\GenKeyCreds\`.
  
## Register the app and upload public key to Azure AD

Next we need to provide the public key for our new certificate to Azure AD so they can verify the signature on our token requests.

1. Open your browser and go to https://manage.windowsazure.com. Sign in with an account that has the ability to administer your Azure subscription.

1. In the left-hand navigation, select **Active Directory**.

  ![](.\images\azure-portal-ad.PNG)
  
1. In the main window, select **Microsoft**.

  ![](.\images\azure-portal-msft.PNG)
  
1. Select **Applications** in the top nav.

  ![](.\images\azure-portal-apps.PNG)
  
1. Select **Add** in the bottom toolbar.

  ![](.\images\azure-portal-add-app.PNG)
  
1. In the dialog, select **Add an application that my organization is developing**.

1. Give the application a descriptive name, and make sure **Web application and/or Web API** is selected for the **Type**. Select the **Next** button (forward arrow).

1. In the next page, enter `http://localhost:8080/java-calendar-demo/SignUp` for the **Sign-on URL**, and then enter a unique URI for the **App-Id URI**. This value just has to be unique, so I recommend generating it by using your Office 365 domain and a unique value. For example: `https://contoso.onmicrosoft.com/java-calendar-demo`. Select the **Complete** button (check-mark) to create the app.

1. Once the app is created in the portal, select the **Configure** link at the top of the page.

1. Locate the **Client ID** field and copy the value. Save this value somewhere, we'll need it later.

1. Locate the **Permissions to other applications** section. Select the **Add Application** button. Select **Microsoft Graph** and select the **Complete** button (check-mark at bottom).

1. Expand the **Application Permissions** drop down and select the following permissions:
  - Read directory data
  - Read and write calendars in all mailboxes
  
  ![](.\images\azure-portal-permissions.PNG)
  
1. Select the **Save** button in the bottom toolbar. Wait for the save operation to complete, then select the **Manage Manifest** button in the bottom toolbar, and choose **Download Manifest**. Download the manifest to your machine.

1. Open the manifest file you just downloaded in a text editor. Locate the `keyCredentials` value, which should look like the following:

  ```json
  "keyCredentials": [],
  ```
  
1. Use the `GenKeyCreds` app to generate a value for `keyCredentials` in app manifest.
  1. Open a shell/command prompt in the `./GenKeyCreds` directory.
  1. Run the `GenKeyCreds` java app, passing the `calendardemo.cer` file as an argument:
    
    ```shell
    java GenKeyCreds calendaredemo.cer
    ```
    
  1. Open the `keycredentials.txt` file that is generated. Replace the `keyCredentials` line in the downloaded manifest with the contents of this file. Save your changes. It should look similar to this (values truncated for readability):
  
    ```json
    ...
    "identifierUris": [
      "https://contoso.onmicrosoft.com/java-calendar-demo"
    ],
    "keyCredentials": [
      {
        "customKeyIdentifier": "LcsZc6fLX3Z5...",
        "keyId": "667a3ae1-47a4-47f8-9566-fc2e7213682e",
        "type": "AsymmetricX509Cert",
        "usage": "Verify",
        "value": "MIIDdTCCAl2gAwIBAgIEIR7RTDANBgkqhkiG9w0BAQsFADBrMQswCQYDVQQGEwJVUzETMBEGA1UECBMKV2FzaGluZ3RvbjERMA8GA1UEBxMIQmVsbGV2dWUxEDAOBgNVBAoTB0NvbnRvc28xDTALBgNVBAsTBENvcnAxEzARBgNVBAMTClNhcmEgRGF2aXMwHhcNMTYwNDA4MTQzMTAzWhcNMTcwNDAzMTQzMTAzWjBrMQswCQYDVQQGEwJVUzETMBEGA1UECBMKV2FzaGluZ3RvbjERMA8GA1UEBxMIQmVsbGV2dWUxEDAOBgNVBAoTB0NvbnRvc28xDTALBgNVBAsTBENvcnAxEzARBgNVBAMTClNhcmEgRGF2aXMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwgg..."
      }
    ],
    "knownClientApplications": [],
    "logoutUrl": null,
    ...
    ```
  
  1. Switch back to the Azure management portal. Select the **Manage Manifest** button and choose **Upload Manifest**. Browse to the manifest file you just updated and upload.
  
## Run the sample

1. Open the `java-calendar-demo` project in Eclipse. Open the `./src/main/java/com/outlook/dev/calendardemo/AuthHelper.java` file and replace the `YOUR CLIEN ID HERE` string with the client ID you copied after creating the app in Azure.
1. Replace the `YOUR CERT THUMBPRINT HERE` with the value of `customKeyIdentifier` from your app's manifest.
1. Save the file and run the app.
1. Browse to `http://localhost:8080/java-calendar-demo`.