# focusGuard

A small Android app that helps you stop reaching for your phone out of habit.

When focus mode is on and you unlock the phone, a card appears over the screen asking **"Are you sure you need to check something on your phone?"**, along with when you last unlocked it, how many times you've unlocked it today, and a **word of unlock** — an English word with its Polish translation. The OK button unlocks after 2 seconds.

- Only asks when you unlock too often (previous unlock within a chosen time, 2 h by default, or every time)
- Today's unlock count and a 7-day chart; history kept for 4 weeks
- Quick Settings tile to switch focus mode on and off
- Dark mode, starts after a reboot, uses almost no battery
- Fully offline: no account, no network, all data stays on the phone

Built with Kotlin and Jetpack Compose, Android 8.0+ (tested on Android 16).
