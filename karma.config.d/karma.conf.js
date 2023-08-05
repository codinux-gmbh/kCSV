config.set({
    client: {
        mocha: {
            timeout: 20000 // Mocha times out after 2 s, which is too short for bufferExceeded() test
        }
    }
})