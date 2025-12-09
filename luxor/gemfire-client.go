package main

import (
    "fmt"
    "github.com/luxorgroup/luxor-go"
)

func main() {
    config := luxor.Config{
        Servers: []string{"locator1:10334", "locator2:10334"},
        Username: "admin",
        Password: "password",
    }

    client, err := luxor.NewClient(config)
    if err != nil {
        panic(err)
    }
    defer client.Close()

    // Put
    err = client.Put("myRegion", "key1", "hello")
    if err != nil {
        panic(err)
    }

    // Get
    val, err := client.Get("myRegion", "key1")
    if err != nil {
        panic(err)
    }

    fmt.Println("Value:", val)
}
