# birthdays

Clojure-powered automated email sender for birthdays and anniversaries.

Currently only works with Gmail (as the sender email address).

## Configuration

This project uses [Omniconf](https://github.com/grammarly/omniconf) to get application-level preferences and secrets
into code. See `src/birthdays/config.clj` for a list of config options. Less obvious values are explicitly mentioned
below.

Secret values should find there way into the code as GitHub Actions secrets -> environment variables -> read into config
by Omniconf.

### Sender email

Save this as a GitHub Actions secret named `SENDER_EMAIL`.

### Google App Password

You need a Google App Password to send emails on behalf of your Gmail account. This can be
done [here](https://myaccount.google.com/apppasswords).

Save this as a GitHub Actions secret named `GMAIL_PASSWORD`.

## Execution

### Locally

```shell
clojure -M -m birthdays.birthdays
```

### Remotely

This program runs at 07:00 UTC daily in a GitHub Actions workflow from the commandline, scheduled by cron.

## License

Distributed under an [MIT License](./LICENSE).
