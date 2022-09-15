# Contributing to the Java Client API

The Java Client API welcomes new contributors. This document will guide you 
through the process.

 - [Question or Problem?](#question)
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 
## <a name="question"></a> Got a Question or Problem?

If you have questions about how to use the Java Client API, you can ask on 
[StackOverflow](http://stackoverflow.com/tags/marklogic), tagging the question 
with MarkLogic.

## <a name="issue"></a> Found an Issue?
If you find a bug in the source code or a mistake in the documentation, you can help us by
submitting an issue to our [GitHub Issue
Tracker](https://github.com/marklogic/java-client-api/issues). Even better you
can submit a Pull Request with a fix for the issue you filed.

## <a name="feature"></a> Want a Feature?
You can request a new feature by submitting an issue to our 
[GitHub Issue Tracker](https://github.com/marklogic/java-client-api/issues).
If you would like to implement a new feature then first create a new issue and
discuss it with one of our project maintainers.

## <a name="submit"></a> Submission Guidelines

### Submitting an Issue
If your issue appears to be a bug, and hasn't been reported, open a new issue.
Providing the following information will increase the chances of your issue 
being dealt with quickly:

* **Overview of the Issue** - if an error is being thrown a stack trace helps
* **Motivation for or Use Case** - explain why this is a bug for you
* **Environment** - Mac, windows? Firefox, Chrome? details help
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point 
to what might be causing the problem (line of code or commit)

### Submitting a Pull Request

#### Fill in the CLA

Before we can accept your pull request, we need you to sign the [Contributor 
License Agreement](http://developer.marklogic.com/products/cla). 

#### Fork the Java Client API

Fork the project [on GitHub](https://github.com/marklogic/java-client-api/fork) 
and clone your copy.

    $ git clone git@github.com:username/java-client-api.git
    $ cd java-client-api
    $ git remote add upstream git://github.com/marklogic/java-client-api.git

All bug fixes and new features go into the
[develop](https://github.com/marklogic/java-client-api/tree/develop) branch.

We ask that you open an issue in the
[issue tracker](https://github.com/marklogic/java-client-api/issues)
and get agreement from at least one of the project maintainers before you start
coding.

Nothing is more frustrating than seeing your hard work go to waste because
your vision does not align with that of a project maintainer.

#### Choose the appropriate branch

While the \*master branches reflect the latest released code for the
corresponding server release, the \*develop branches reflect the latest changes
that will be released next and are therefore the right place to commit changes.
To be clear, the following branches have the assigned meaning:
* develop - the place to make changes for the next release
* master  - the stable code from the last release
* 3.0-develop - the place to make changes for the next 3.x release (corresponds with the next server 8.x release)
* 3.0-master  - the stable code from the last 3.x release (corresponds with the latest server 8.x release)
* 2.0-develop - the place to make changes for the next 2.x release (corresponds with the next server 7.x release)
* 2.0-master  - the stable code from the last 2.x release (corresponds with the latest server 7.x release)
* 1.0-develop - the place to make changes for the next 1.x release (corresponds with the next server 6.x release)
* 1.0-master  - the stable code from the last 1.x release (corresponds with the latest server 6.x release)

#### Create a branch for your changes

Okay, so you have decided to fix something. Create a feature branch
and start hacking:

    $ git checkout -b my-feature-branch -t origin/develop

#### Commit your changes

Make sure git knows your name and email address:

    $ git config --global user.name "J. Random User"
    $ git config --global user.email "j.random.user@example.com"

Writing good commit logs is important. A commit log should describe what
changed and why. Follow these guidelines when writing one:

1. The first line should be 50 characters or less and contain a short
   description of the change including the Issue number prefixed by a hash (#).
2. Keep the second line blank.
3. Wrap all other lines at 72 columns.

A good commit log looks like this:

```
Fixing Issue #123: make the whatchamajigger work in MarkLogic 8

Body of commit message is a few lines of text, explaining things
in more detail, possibly giving some background about the issue
being fixed, etc etc.

The body of the commit message can be several paragraphs, and
please do proper word-wrap and keep columns shorter than about
72 characters or so. That way `git log` will show things
nicely even when it is indented.
```

The header line should be meaningful; it is what other people see when they
run `git shortlog` or `git log --oneline`.

#### Rebase your repo

Use `git rebase` (not `git merge`) to sync your work from time to time.

    $ git fetch upstream
    $ git rebase upstream/develop

#### Test your code

Be sure to run the tests before submitting your pull request. PRs with failing
tests won't be accepted. See the instructions in this repository's README.md file for running the tests.


#### If your server is not on localhost

You can either create SSH tunnels to your server on ports 8000, 8002, and 8012
by a command like:

    ssh -L 8000:localhost:8000 -L 8002:localhost:8002 -L 8012:localhost:8012 user@hostname

Or you can update the static `HOST` field in
src/test/java/com/marklogic/client/test/Common.java
and the `example.host` property in src/main/resources/Example.properties.

#### Push your changes

    $ git push origin my-feature-branch

#### Submit the pull request

Go to https://github.com/username/java-client-api and select your feature 
branch. Click the 'Pull Request' button and fill out the form.

Pull requests are usually reviewed within a few days. If you get comments that 
need to be to addressed, apply your changes in a separate commit and push that 
to your feature branch. Post a comment in the pull request afterwards; GitHub 
does not send out notifications when you add commits to existing pull requests.

That's it! Thank you for your contribution!


#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull 
the changes from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your 
local shell as follows:

    $ git push origin --delete my-feature-branch

* Check out the *develop* branch:

    $ git checkout develop -f

* Delete the local branch:

    $ git branch -D my-feature-branch

* Update your *develop* with the latest upstream version:

    $ git pull --ff upstream develop
