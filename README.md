# birthdays

Clojure-powered automated email sender for birthdays and anniversaries.

Works with Gmail or Zoho, currently.

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
git pull --rebase public main
git push origin main
```

## Configuration

This project uses [Omniconf](https://github.com/grammarly/omniconf) to get application-level preferences and secrets
into code. See `src/birthdays/config.clj` for a list of config options. Less obvious values are explicitly mentioned
below.

Secret values should find there way into the code as GitHub Actions secrets -> environment variables -> read into config
by Omniconf.

### :email-provider

If you use Gmail, this value should be `gmail`. If you use Zoho Mail, this value should be `zoho`.

Save this as a GitHub Actions secret named `EMAIL_PROVIDER`.

### :sender-email

Save this as a GitHub Actions secret named `SENDER_EMAIL`.

### :password

Save this as a GitHub Actions secret named `PASSWORD`.

#### Gmail

You need a Google App Password to send emails on behalf of your Gmail account. This can be
done [here](https://myaccount.google.com/apppasswords).

#### Zoho

If you have multifactor authentication (MFA) enabled for your Zoho Mail account, you will need to create an
_Application-Specific Password_ -
see [their docs](https://www.zoho.com/mail/help/adminconsole/two-factor-authentication.html#alink7). Even if you don't
have MFA enabled, it is still a good idea to create one of these. Otherwise, a bad actor that gets their hands on your
password will have access to everything in your account.

### :cc

Emails to cc (can be omitted entirely). Save this as a GitHub Actions secret named `CC`.

For one cc recipient, e.g.:

```shell
export CC=person1@mail.com
```

For multiple cc recipients, e.g.:

```shell
export CC=person1@mail.com,person2@mail.com
```

### :footer

Goes at the bottom of every email. Configure in the `config` namespace.

### :input-file

Path to file holding birthdays and anniversaries data. Configure in the `config` namespace.

## Execution

### Locally

```shell
clojure -M -m birthdays.birthdays
```

### Remotely

This program runs at 07:00 UTC daily in a GitHub Actions workflow from the commandline, scheduled by cron.

## License

Distributed under an [MIT License](./LICENSE).
