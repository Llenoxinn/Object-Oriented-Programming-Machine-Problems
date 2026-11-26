# Lab Exercise 1 (C) — Type-Tagged Variable & Array Logger

A lightweight, header-only C logging utility (`logger.h`) that inspects scalar variables and arrays at runtime through `void*` pointers paired with a type tag enum.

## Contents

- [`logger.h`](logger.h) — Single-header C library implementing `VAR_TYPE`, `log_variable`, and `log_array`.

## API Overview

### `VAR_TYPE`

Type discriminator used to interpret generic `void*` addresses:

```c
typedef enum {
    CHAR,
    INT,
    FLOAT,
    DOUBLE
} VAR_TYPE;
```

### `log_variable`

```c
void* log_variable(char* label, void* var_address, VAR_TYPE var_type);
```

Dereferences `var_address` according to `var_type`, prints the labeled value followed by a newline, and returns `var_address`.

### `log_array`

```c
void* log_array(char* label, void* arr_address, int array_size, VAR_TYPE var_type);
```

Iterates over `arr_address` according to `var_type`, prints the labeled elements on a single line followed by a newline, and returns `arr_address`.

- For `CHAR` buffers, characters are printed contiguously until `'\0'` or `array_size` is reached.
- For numeric types (`INT`, `FLOAT`, `DOUBLE`), elements are separated by spaces.

## Implementation Variants

`logger.h` keeps two selectable builds gated by the `LOGGER_USE_IMPROVED` preprocessor flag:

| Build Mode | Flag | Notes |
| --- | --- | --- |
| Original (default) | *(none)* | Initial implementation preserved as written. |
| Improved | `-DLOGGER_USE_IMPROVED` | Standardizes `<label>: <value>` spacing and prints every element from index `0` to `array_size - 1` with clean single-space delimiters. |

## Usage Example

```c
#define LOGGER_USE_IMPROVED
#include "logger.h"

int main(void) {
    int count = 42;
    double readings[4] = {18.5, 20.0, 21.25, 19.75};
    char status[32] = "nominal";

    log_variable("count", &count, INT);
    log_array("readings", readings, 4, DOUBLE);
    log_array("status", status, 32, CHAR);

    return 0;
}
```

Compile and run with `gcc`:

```bash
gcc -DLOGGER_USE_IMPROVED main.c -o demo && ./demo
```

Sample output:

```text
count: 42
readings: 18.500000 20.000000 21.250000 19.750000
status: nominal
```
