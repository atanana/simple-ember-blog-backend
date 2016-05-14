package controllers

import play.api.mvc.{Action, Controller}
import views.html.main

class ApplicationController extends Controller {
  def index(path: String) = Action {
    Ok(main.render())
  }
}
