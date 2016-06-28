import Controller from './Controller';
import RequestsView from '../views/requests';
import { FetchAction } from '../actions/api/requests';

class RequestsTableController extends Controller {

  initialize({store, subFilter, searchFilter}) {
    app.showPageLoader()
    this.title('Requests');
    this.store = store;
    this.subFilter = subFilter;
    this.searchFilter = searchFilter;

    this.refresh().then(() => {
      this.setView(new RequestsView(this.store, this.subFilter, this.searchFilter, (...args) => this.updateFilters(...args)));
      app.showView(this.view);
    });
  }

  refresh() {
    let promises = [];
    promises.push(this.store.dispatch(FetchAction.trigger(this.state)));
    return Promise.all(promises);
  }

  // Backbone router sucks
  updateFilters(state, subFilter, searchFilter) {
    this.subFilter = subFilter;
    this.searchFilter = searchFilter;
  }
}

export default RequestsTableController;