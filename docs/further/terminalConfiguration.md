## Terminal configuration


### preconfigured terminals


Set the parameter _terminal_ in the cobol block in your build gradle, to use one of the preconfigured terminals.

| terminal | value |
| -------- | ----- |
| gnome-terminal | 'gnome-terminal' |


### configure own terminal

Set the parameter _customTerminal_ in the cobol clock in your build.gradle to use a custom terminal commands.

Insert the full qualified terminal command string. Use `{path}` as placeholder for the absolute path to the executable.
