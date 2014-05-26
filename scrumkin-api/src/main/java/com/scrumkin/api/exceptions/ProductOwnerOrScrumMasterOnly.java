package com.scrumkin.api.exceptions;

public class ProductOwnerOrScrumMasterOnly extends Exception {
    public ProductOwnerOrScrumMasterOnly() {
        super("User cannot be scrum master and product owner at the same time.");
    }
}
