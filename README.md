# birthdays

Clojure-powered automated email sender for birthdays and anniversaries.

Currently only works with Gmail (as the sender email address).

## Cloning/forking

On GitHub, a fork of a public
repository [must also be public](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/about-forks):

> A fork is a new repository that shares code and visibility settings with the original “upstream” repository.

(One nice thing about a GitHub fork is that syncing with the original project can be done easily on the GitHub UI.)

*However,* it's probably not a good idea to expose your friends' personal details, so one fairly simple way of making
use of this project in a private repo (while maintaining the ability to periodically integrate upstream changes)
is to create a private fork—see [this Stack Overflow answer](https://stackoverflow.com/a/30352360/4304123) for a
step-by-step guide (unfortunately, this means that syncing your repo with upstream changes via GitHub's UI isn't
possible).

### Example

#### Duplicate the code

```shell
git clone --bare https://github.com/blairnangle/birthdays.git
cd birthdays.git
git push --mirror https://github.com/yourname/birthdays-private.git
cd ..
rm -rf birthdays.git
```

#### Get the cloned project on your local machine

```shell
git clone https://github.com/yourname/birthdays-private.git
cd birthdays-private
[make some changes]
git commit
git push origin main
```

#### Sync with upstream (public) changes by creating a new remote

```shell
git remote add public https://github.com/blairnangle/birthdays.git
git pull public main
git push origin main
```

## Configuration

This project uses [Omniconf](https://github.com/grammarly/omniconf) to get application-level preferences and secrets
into code. See `src/birthdays/config.clj` for a list of config options. Less obvious values are explicitly mentioned
below.

Secret values should find there way into the code as GitHub Actions secrets -> environment variables -> read into config
by Omniconf.

### :sender-email

Save this as a GitHub Actions secret named `SENDER_EMAIL`.

### :password

#### Gmail

You need a Google App Password to send emails on behalf of your Gmail account. This can be
done [here](https://myaccount.google.com/apppasswords).

Save this as a GitHub Actions secret named `GMAIL_PASSWORD`.

#### Zoho

If you have multifactor authentication (MFA) enabled for your Zoho Mail account, you will need to create an
_Application-Specific Password_ -
see [their docs](https://www.zoho.com/mail/help/adminconsole/two-factor-authentication.html#alink7). Even if you don't
have MFA enabled, it is still a good idea to create one of these. Otherwise, a bad actor that gets their hands on your
password will have access to everything in your account.

## Execution

### Locally

```shell
clojure -M -m birthdays.birthdays
```

### Remotely

This program runs at 07:00 UTC daily in a GitHub Actions workflow from the commandline, scheduled by cron.

## License

Distributed under an [MIT License](./LICENSE).
