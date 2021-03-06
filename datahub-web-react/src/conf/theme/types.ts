export type Theme = {
    styles: {
        'border-radius-base': string;
        'layout-header-background': string;
        'layout-header-color': string;
        'layout-body-background': string;
        'component-background': string;
        'body-background': string;
        'border-color-base': string;
        'text-color': string;
        'text-color-secondary': string;
        'heading-color': string;
        'background-color-light': string;
        'divider-color': string;
        'disabled-color': string;
        'steps-nav-arrow-color': string;
        'homepage-background-upper-fade': string;
        'homepage-background-lower-fade': string;
    };
    assets: {
        logoUrl: string;
    };
    content: {
        title: string;
        homepage: {
            homepageMessage: string;
        };
        search: {
            searchbarMessage: string;
        };
    };
};
