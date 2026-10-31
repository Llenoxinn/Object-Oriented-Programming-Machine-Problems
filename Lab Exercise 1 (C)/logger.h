#include <stdio.h>

typedef enum {
    CHAR,
    INT,
    FLOAT,
    DOUBLE
} VAR_TYPE;

/* ===== Original version (default). Define LOGGER_USE_IMPROVED to use the improved one below. ===== */
#ifndef LOGGER_USE_IMPROVED
void* log_variable(char* label, void* var_address, VAR_TYPE var_type) {
    printf("%s:", label);
    switch(var_type) {
        case CHAR: {
            printf("%c",*(char*)var_address);
            break;
        }
        case INT: {
            printf("%i",*(int*)var_address);
            break;
        }
        case FLOAT: {
            printf("%f",*(float*)var_address);
            break;
        }
        case DOUBLE: {
            printf("%lf",*(double*)var_address);
            break;
        }
    }
    printf("\n");
    return var_address;
}
void* log_array(char* label, void* arr_address, int array_size, VAR_TYPE var_type) {
    printf("%s: ", label);
    if(var_type == INT) {
        int *arr = arr_address;
        for(int i = 0; i<array_size-1; i++) {
            printf(" %i", arr[i]);
        }
    } else if(var_type == CHAR) {
        char *arr = arr_address;
        for(int i = 0; i<array_size && arr[i] != '\0'; i++) {
            printf("%c", arr[i]);
        }
    } else if(var_type == FLOAT) {
        float *arr = arr_address;
        for(int i = 0; i<array_size-1; i++) {
            printf(" %f", arr[i]);
        }
    } else {
        double *arr = arr_address;
        for(int i = 0; i<array_size-1; i++) {
            printf(" %lf", arr[i]);
        }
    }
    printf("\n");
    return arr_address;
}
#endif /* LOGGER_USE_IMPROVED */

/* ===== Improved version: matches the README format "<label>: <value>" and prints every element ===== */
#ifdef LOGGER_USE_IMPROVED
void* log_variable(char* label, void* var_address, VAR_TYPE var_type) {
    printf("%s: ", label);
    switch (var_type) {
        case CHAR:   printf("%c", *(char*)var_address); break;
        case INT:    printf("%d", *(int*)var_address); break;
        case FLOAT:  printf("%f", *(float*)var_address); break;
        case DOUBLE: printf("%f", *(double*)var_address); break;
    }
    printf("\n");
    return var_address;
}

void* log_array(char* label, void* arr_address, int array_size, VAR_TYPE var_type) {
    printf("%s: ", label);
    if (var_type == CHAR) {
        /* strings: no separators, stop at '\0' or array_size */
        char* arr = arr_address;
        for (int i = 0; i < array_size && arr[i] != '\0'; i++) {
            printf("%c", arr[i]);
        }
    } else {
        for (int i = 0; i < array_size; i++) {
            if (i > 0) printf(" ");
            switch (var_type) {
                case INT:    printf("%d", ((int*)arr_address)[i]); break;
                case FLOAT:  printf("%f", ((float*)arr_address)[i]); break;
                case DOUBLE: printf("%f", ((double*)arr_address)[i]); break;
                default: break;
            }
        }
    }
    printf("\n");
    return arr_address;
}
#endif /* LOGGER_USE_IMPROVED */
