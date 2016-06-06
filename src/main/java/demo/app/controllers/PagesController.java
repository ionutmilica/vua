package demo.app.controllers;

import vua.http.Redirect;
import vua.http.Response;
import vua.routing.Controller;

public class PagesController extends Controller {

    public Response about() {
        return new Response("About!");
    }

    public Response contact() {
        return Redirect.to("http://youtube.com");
    }
}