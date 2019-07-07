package com.coditory.sherlock.base

class SpecSimulatedException extends RuntimeException {
  SpecSimulatedException() {
    this("Simulated exception for test")
  }

  SpecSimulatedException(String message) {
    super(message)
  }
}
