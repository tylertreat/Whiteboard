package com.whiteboard.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.clarionmedia.infinitum.logging.Logger;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.service.NotificationService;

@Bean("notificationService")
public class NotificationServiceImpl implements NotificationService {

    private static final String SES_ACCESS_KEY = "AKIAJQCSS6HWHET2LWNQ";
    private static final String SES_SECRET_KEY = "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";
    private static final String EMAIL_SENDER_ADDRESS = "ttreat31@gmail.com";
    private static final String EMAIL_SUBJECT = "Whiteboard Invitation";
    private static final String EMAIL_BODY = "Greetings, %s!\n\n%s has invited you to participate in a whiteboard. " +
            "To view it, open the following link from your Android device:\n\n%s\n\nRegards,\nThe Whiteboard Team";

    @Override
    public void emailWhiteboardInvite(WhiteboardDocument whiteboard, String name, String email) {
        AmazonSimpleEmailServiceClient sesClient = getClient();
        String subjectText = EMAIL_SUBJECT;
        Content subjectContent = new Content(subjectText);
        String bodyText = String.format(EMAIL_BODY, name, whiteboard.getOwner(), "http://www.google.com/");
        Body messageBody = new Body(new Content(bodyText));
        Message inviteMessage = new Message(subjectContent, messageBody);
        Destination destination = new Destination().withToAddresses(email);

        SendEmailRequest request = new SendEmailRequest(EMAIL_SENDER_ADDRESS, destination, inviteMessage);
        Logger.getInstance(getClass().getSimpleName()).debug("Sending email to " + email);
        sesClient.sendEmail(request);
    }

    private AmazonSimpleEmailServiceClient getClient() {
        AWSCredentials credentials = new BasicAWSCredentials(SES_ACCESS_KEY, SES_SECRET_KEY);
        return new AmazonSimpleEmailServiceClient(credentials);
    }

}
