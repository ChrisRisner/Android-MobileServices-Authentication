# Android - Mobile Services - Authentication
This is an authentication sample which makes use of Windows Azure Mobile Services.  Mobile Services offers built in authentication with Facebook, Google, Microsoft, and Twitter as well as the possibility to implement your own custom authentication.  This sample was built using Eclipse, the Android SDK, and the Andorid Mobile Services SDK.  It was built using a minimum SDK version of 8 and a target version of 17.  

Below you will find requirements and deployment instructions.

## Requirements
* Eclipse - This sample was built with Eclipse Indigo.
* Android SDK - You can download this from the [Android Developer portal](http://developer.android.com/sdk/index.html).
* Windows Azure Account - Needed to create and run the Mobile Service.  [Sign up for a free trial](https://www.windowsazure.com/en-us/pricing/free-trial/).
* Forked Mobile Services SDK - A few changes were made to the Android Mobile Services SDK.  Please read the section on that below.

## Source Code Folders
* /source/client - This contains code for the application with Mobile Services and requires client side changes noted below.
* /source/scripts - This contains copies of the server side scripts and requires script changes noted below.

## Additional Resources
I've released two blog posts which walks through the code for this sample.  The [first deals with the server side scripts](http://chrisrisner.com/Authentication-with-Mobile-Services) and talks about how to set up the different auth providers.  The [second talks about the Android Client](http://chrisrisner.com/Authentication-with-Android-and-Windows-Azure-Mobile-Services) and how to connect that to the Mobile Service.

## Android Mobile Service SDK Fork
In order to facilitate retrying requests, some changes were made to the Android SDK for Mobile Services.  You can access the forked repo for the SDK [here](https://github.com/ChrisRisner/azure-mobile-services/tree/RetrySupport).  Once downloading the forked SDK, import it into Eclipse.  You should then be able to reference it from this project.  If you don't want to offer retry request support, the forked SDK is not necessary.  You'll just need to drop in the jars that come as part of the Mobile Services SDK for Android.

#Setting up your Mobile Service
After creating your Mobile Service in the Windows Azure Portal, you'll need to create tables named "Accounts", "AuthData", and "BadAuth".  After creating these tables, copy the appropriate scripts over.

#Client Application Changes
In order to run the client applicaiton, you'll need to change a few settings in your application.  After importing the project into Eclipse, open AuthService.java file.  In the constructor method, change the <mobileserviceurl> and <applicationkey> to match the values from the Mobile Service you've created.

#Script Changes
Inside of the accounts.insert.js script, you'll need to set the masterKey variable to the master key of your Mobile Service.  This can be accessed by going to the Dashboard for your Mobile Service in the Windows Azure portal and clicking Manage Keys at the bottom of the screen.

## Contact

For additional questions or feedback, please contact the [team](mailto:chrisner@microsoft.com).