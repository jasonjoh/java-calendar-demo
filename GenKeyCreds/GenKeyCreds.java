// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class GenKeyCreds {

	public static void main(String[] args) {
		if (args.length < 1){
			System.out.println("Please provide a DER-encoded certificate file.");
			return;
		}
		
		String certFile = args[0];
		System.out.printf("Generating keyCredentials entry from %s\n", certFile);

		try {
			FileInputStream certFileIn = new FileInputStream(certFile);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(certFileIn);
			
			// Generate base64-encoded version of the cert's data
			// for the "value" property of the "keyCredentials" entry
			byte[] certData = cert.getEncoded();
			String certValue = Base64.getEncoder().encodeToString(certData);
			System.out.println("Cert value: " + certValue);
			
			// Generate the SHA1-hash of the cert for the "customKeyIdentifier"
			// property of the "keyCredentials" entry
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(certData);
			String certCustomKeyId = Base64.getEncoder().encodeToString(md.digest());
			System.out.println("Cert custom key ID: " + certCustomKeyId);
			
			FileWriter fw = new FileWriter("keycredentials.txt", false);
			PrintWriter pw = new PrintWriter(fw);
			
			pw.println("\"keyCredentials\": [");
			pw.println("  {");
			pw.println("    \"customKeyIdentifier\": \"" + certCustomKeyId + "\",");
			pw.println("    \"keyId\": \"" + UUID.randomUUID().toString() + "\",");
			pw.println("    \"type\": \"AsymmetricX509Cert\",");
			pw.println("    \"usage\": \"Verify\",");
			pw.println("    \"value\": \"" + certValue + "\"");
			pw.println("  }");
			pw.println("],");
			
			pw.close();
			
			System.out.println("Key credentials written to keycredentials.txt");
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: Cannot find %s\n", certFile);
		} catch (CertificateException e) {
			System.out.println("ERROR: Cannot instantiate X.509 certificate");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("ERROR: Cannot instantiate SHA-1 algorithm");
		} catch (IOException e) {
			System.out.println("ERROR: Cannot write to keycredentials.txt");
		}
	}

}

// MIT License:

// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// ""Software""), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:

// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.