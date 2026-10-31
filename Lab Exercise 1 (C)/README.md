Lab Exercise 1 (Imperative Programming Review)
Create a C library that contains the following functions.

Link to uvec vpl

void* log_variable(char* label, void* var_address, VAR_TYPE var_type)

This function prints the current value of the variable at the address var_address The datatype will be indicated by an enum VAR_TYPE called var_type. The argument label can be any string.

This function should print one log line according to the following format.

<label>: <var_adress value>

There should be a newline after the logline.

void* log_array(char* label, void* arr_address, int array_size, VAR_TYPE var_type)

This function prints the current value of the array at the address arr_address The datatype will be indicated by the enum VAR_TYPE called var_type. The argument label can be any string. The argument array_size corresponds to the number of elements in the array. The elements should be printed in one line, separated by spaces.

For example, given a three element array, the function should print one log line according to the following format.

<label>: <elem1> <elem2> <elem3>

If the array passed is a char array or string, there should be no spaces between the characters.

<label>: <string>

Here is an example of the function being used in primality testing.

#include <stdio.h>
#include "logger.h"

void main() {
    int x = 187;
    int prime_flag = 0;
    for (int cf = 2; cf * cf <= x; cf++) {
        log_variable("cf", &cf, INT);
        if (x % cf == 0) {
            log_variable("cf inside if", &cf, INT);
            printf("%d is composite!\n", x);
            prime_flag = 1;
            break;
        }
    }
    if (prime_flag == 0) {
        printf("%d is prime!\n", x);
    }
}

This program should print:

cf: 2
cf: 3
cf: 4
cf: 5
cf: 6
cf: 7
cf: 8
cf: 9
cf: 10
cf: 11
cf inside if: 11
187 is composite!

Here’s another example with the logger used on a sorting procedure.

#include <stdio.h>
#include "logger.h"

void main() {
    int size = 7;
    int a[7] = {1, -2, 0, -2, 3, 3, 4};
    for (int i = 0; i < size - 1; i++) {
        int j = i + 1;
        log_variable("i", &i, INT);
        while (a[j] < a[j-1]) {
            int temp = a[j-1];
            a[j-1] = a[j];
            a[j] = temp;
            j--;
        }
        log_array("sort progress", a, size, INT);
    }
}

i: 0
sort progress: -2 1 0 -2 3 3 4
i: 1
sort progress: -2 0 1 -2 3 3 4
i: 2
sort progress: -2 -2 0 1 3 3 4
i: 3
sort progress: -2 -2 0 1 3 3 4
i: 4
sort progress: -2 -2 0 1 3 3 4
i: 5
sort progress: -2 -2 0 1 3 3 4

Another example with strings:

#include <stdio.h>
#include "logger.h"

void printTokens(char *str) {
    char *token;
    token = strtok(str, ".");
    while (token != NULL) {
        printf("%s\n", token);
        token = strtok(NULL, ".");
    }
}

void main() {
    char str[100] = "hello.everyone.and.world";
    log_array("str before tokenization", str, 100, CHAR);
    printTokens(str);
    log_array("str after tokenization", str, 100, CHAR);
}

str before tokenization: hello.everyone.and.world
hello
everyone
and
world
str after tokenization: hello

---

# Improved Version

# Lab Exercise 1: Imperative Programming Review

Create a C library, `logger.h`, that provides two logging functions for tracing variables and arrays while a program runs.

Link to uvec vpl: _(add link here)_

## Data type enum

The type of the data being logged is given by the enum `VAR_TYPE`:

```c
typedef enum {
    CHAR,
    INT,
    FLOAT,
    DOUBLE
} VAR_TYPE;
```

## Functions

### `log_variable`

```c
void* log_variable(char* label, void* var_address, VAR_TYPE var_type);
```

Prints the current value of the variable stored at `var_address`.

| Parameter     | Description                                    |
| ------------- | ---------------------------------------------- |
| `label`       | Any string, printed before the value.          |
| `var_address` | Address of the variable to print.              |
| `var_type`    | A `VAR_TYPE` giving the variable's data type.  |

**Output format** (one line, ending with a newline):

```
<label>: <value>
```

**Returns:** `var_address`.

### `log_array`

```c
void* log_array(char* label, void* arr_address, int array_size, VAR_TYPE var_type);
```

Prints the current contents of the array stored at `arr_address`.

| Parameter      | Description                                    |
| -------------- | ---------------------------------------------- |
| `label`        | Any string, printed before the elements.       |
| `arr_address`  | Address of the first element of the array.     |
| `array_size`   | Number of elements in the array.               |
| `var_type`     | A `VAR_TYPE` giving the element data type.     |

**Output format:** all elements on one line, separated by single spaces, ending with a newline.

```
<label>: <elem1> <elem2> <elem3>
```

**Char arrays and strings:** print the characters with no spaces between them. Printing stops at the first `'\0'` or after `array_size` characters, whichever comes first.

```
<label>: <string>
```

**Returns:** `arr_address`.

## Examples

### Primality testing

```c
#include <stdio.h>
#include "logger.h"

int main() {
    int x = 187;
    int prime_flag = 0;
    for (int cf = 2; cf * cf <= x; cf++) {
        log_variable("cf", &cf, INT);
        if (x % cf == 0) {
            log_variable("cf inside if", &cf, INT);
            printf("%d is composite!\n", x);
            prime_flag = 1;
            break;
        }
    }
    if (prime_flag == 0) {
        printf("%d is prime!\n", x);
    }
    return 0;
}
```

Output:

```
cf: 2
cf: 3
cf: 4
cf: 5
cf: 6
cf: 7
cf: 8
cf: 9
cf: 10
cf: 11
cf inside if: 11
187 is composite!
```

### Sorting

```c
#include <stdio.h>
#include "logger.h"

int main() {
    int size = 7;
    int a[7] = {1, -2, 0, -2, 3, 3, 4};
    for (int i = 0; i < size - 1; i++) {
        int j = i + 1;
        log_variable("i", &i, INT);
        while (j > 0 && a[j] < a[j-1]) {
            int temp = a[j-1];
            a[j-1] = a[j];
            a[j] = temp;
            j--;
        }
        log_array("sort progress", a, size, INT);
    }
    return 0;
}
```

Output:

```
i: 0
sort progress: -2 1 0 -2 3 3 4
i: 1
sort progress: -2 0 1 -2 3 3 4
i: 2
sort progress: -2 -2 0 1 3 3 4
i: 3
sort progress: -2 -2 0 1 3 3 4
i: 4
sort progress: -2 -2 0 1 3 3 4
i: 5
sort progress: -2 -2 0 1 3 3 4
```

### Strings

`strtok` replaces each delimiter with `'\0'`, so the string appears truncated at the first token after tokenization.

```c
#include <stdio.h>
#include <string.h>
#include "logger.h"

void printTokens(char *str) {
    char *token;
    token = strtok(str, ".");
    while (token != NULL) {
        printf("%s\n", token);
        token = strtok(NULL, ".");
    }
}

int main() {
    char str[100] = "hello.everyone.and.world";
    log_array("str before tokenization", str, 100, CHAR);
    printTokens(str);
    log_array("str after tokenization", str, 100, CHAR);
    return 0;
}
```

Output:

```
str before tokenization: hello.everyone.and.world
hello
everyone
and
world
str after tokenization: hello
```
