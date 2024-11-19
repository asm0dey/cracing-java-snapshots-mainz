---
title: CRaCing Java Snapshots
author: "@asm0di0"
extensions: [terminal]
---

# Cracking Java Snapshots

## Pasha Finkelshteyn

---

# `whoami`?

- Geek <!-- stop -->
- Developer 🥑 at Bellsoft <!-- stop --> (we'll talk about it later) <!--  stop -->
- JVM developer: Java and Kotlin <!-- stop --> (and actually Clojure, Scala, Ceylon 😱) <!-- stop -->
- DevOps is not a person, but I could be! <!-- stop -->
- Long-time Linux User <!-- stop --> (Did I say I'm a geek?) <!-- stop -->
- If life gives me a terminal, I have to make a presentation

---

# Why do we need snapshots?

<!-- stop -->

Because Java is slow ;) <!-- stop -->

Just kidding. <!-- stop --> But applications actually are!

<!-- stop -->

```plain
Started PetClinicApplication in 4.932 HOURS
```

↑ That's how muggles see it

---

# Why do we need snapshots?

```terminal-ex
rows: 12
init_text: java -jar /home/finkel/work_bell/spring-petclinic/build/libs/spring-petclinic-3.3.0.jar
command: java -jar /home/finkel/work_bell/spring-petclinic/build/libs/spring-petclinic-3.3.0.jar
init_codeblock: true
init_codeblock_lang: bash
```

---

# How do we tackle it?

---

# CRIU

https://criu.org/Main_Page

> Checkpoint/Restore In Userspace, or **CRIU** (pronounced kree-oo, IPA: /krɪʊ/, Russian: криу),
> is a Linux software. It can freeze a running container (or an individual application) and
> checkpoint its state to disk. The data saved can be used to restore the application and
> run it exactly as it was during the time of the freeze

---

# WOW, that's amazing!

Example:

```bash
for i in $(seq 1 10000); do
  echo $i
  sleep 1
done
```

<!-- stop -->

Run it: `./my.sh`

<!-- stop -->

Wait some time

<!-- stop -->

CRIU it: `criu --shell-job dump -t $(pidof -x ./my.sh)`

<!-- stop -->

Restore it: `criu --shell-job restore`

---

# Demo

---

# It's perfect! 😍

<!-- stop -->

## But

It doesn't _always_ work <!-- stop -->

Demo?

---

# And _of course_ it won't work for Java 😥

<!-- stop -->

But this talk wouldn't be a thing, right?

---

# CRaC

https://openjdk.org/projects/crac/

> The CRaC (Coordinated Restore at Checkpoint) Project researches coordination of Java programs with mechanisms
> to checkpoint (make an image of, snapshot) a Java instance while it is executing. Restoring from the image could
> be a solution to some of the problems with the start-up and warm-up times. The primary aim of the Project is to
> develop a new standard mechanism-agnostic API to notify Java programs about the checkpoint and restore events

---

# CRaC

CRaC is not a canonical part of JDK.

Some JDKs include it, for example Liberica JDK

<!-- stop -->

<blink>Advertising space for rent:</blink>

https://bell-sw.com/pages/downloads/#jdk-21-lts

<!-- stop -->

_CRaC is first implemented by Azul, we support its fork_

---

# What I do and what I do not

- I say CRaC is nice
- I do not say there are no other solutions
- I say developer experience and productivity are good with CRaC
- I do not hide downsides of CRaC, but please ask me if I forgot something

---

# CRaC

Not even all builds support CRaC (it makes the distribution larger)

---

# CRaC example

```java
public static void main(String args[]) throws InterruptedException {
  // This is a part of the saved state
  long startTime = System.currentTimeMillis();
  for(int counter: IntStream.range(1, 100).toArray()) {
  }
}
```

---

# CRaC example

```java
public static void main(String args[]) throws InterruptedException {
  // This is a part of the saved state
  long startTime = System.currentTimeMillis();
  for(int counter: IntStream.range(1, 100).toArray()) {
    Thread.sleep(1000);
  }
}
```

---

# CRaC example

```java
public static void main(String args[]) throws InterruptedException {
  // This is a part of the saved state
  long startTime = System.currentTimeMillis();
  for(int counter: IntStream.range(1, 100).toArray()) {
    Thread.sleep(1000);
    long currentTime = System.currentTimeMillis();
    System.out.println("Counter: " + counter + "(passed " + (currentTime-startTime) + " ms)");
    startTime = currentTime;
  }
}
```

---

# CRaC Example

```terminal7
cat crac2/Dockerfile
```

<!-- stop -->

```terminal-ex
command: "docker build -t pre_crack -f crac2/Dockerfile crac2"
rows: 6
init_codeblock_lang: bash
init_codeblock: true
init_codeblock_lang: bash
init_text: docker build -t pre_crack -f crac2/Dockerfile crac2
```

---

# Now let's crack it and launch from snapshot!

```shell
docker run --cap-add CAP_SYS_PTRACE --cap-add CAP_CHECKPOINT_RESTORE -d pre_crack
```

<!-- stop -->

- `CAP_SYS_PTRACE`: we need to access the whole process tree <!-- stop -->
- `CAP_CHECKPOINT_RESTORE`: somehow there is a special cap for this

<!-- stop -->

I do not add `--rm` because we will need the image later

---

# Now let's crack it and launch from snapshot!

```shell
CRACING=$(docker run --cap-add CAP_SYS_PTRACE --cap-add CAP_CHECKPOINT_RESTORE -d pre_crack)
docker exec -it $CRACING jcmd 129 JDK.checkpoint
docker commit $CRACING post_crack
```

<!-- stop -->

Let's look into our creation

```shell
dive post_crack
```

<!-- stop -->

And run it!

```shell
docker run --rm --entrypoint java post_crack -XX:CRaCRestoreFrom=/app/checkpoint
```

---

# CRaCing Spring

Spring Boot supports CRaC out of the box!

(version 3.2+)

<!-- stop -->

Demo

---

# Is everything that easy?

<!-- stop -->

You guessed id! <!-- stop -->Let's break it!

---

# Breaking the application

Demo

---

# And now it's time to fix it!

Demo

---

# Now you know the basics!

1. CRaC allows to startup any software almost instantly
2. Building Docker with CRaC is not simple but doable
   1. Build jar
   2. Build docker with this jar startup in entrypoint
   3. Run image with `--privileged` or with `--cap-add`
   4. Checkpoint it
   5. Commit
3. To support custom things in application we should
   1. Implement `Resource`
   2. Register it in `Core`

---

# Thank you! Questions?

- Site: https://asm0dey.site
- Bluesky: @asm0dey
- Twitter : @asm0di0
- Mastodon: @asm0dey@fosstodon.org
- LinkedIn: @asm0dey
- E-mail : me@asm0dey.site
