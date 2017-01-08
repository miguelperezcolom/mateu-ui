package io.mateu.ui.core.client;

import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;

/**
 * Created by miguel on 29/12/16.
 */
public class BaseServiceClientSideImpl implements BaseServiceAsync {
    @Override
    public void select(String sql, AsyncCallback<Object[][]> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Object[][] r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).select(sql);

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }



    @Override
    public void selectSingleValue(String sql, AsyncCallback<Object> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Object r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectSingleValue(sql);

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void execute(String sql, AsyncCallback<Void> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).execute(sql);

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(null);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void selectPaginated(Data parameters, AsyncCallback<Data> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).selectPaginated(parameters);

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void set(String serverSideControllerKey, Data data, AsyncCallback<Data> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).set(serverSideControllerKey, data);

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void get(String serverSideControllerKey, Object id, AsyncCallback<Data> callback) {

        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    Data r = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).get(serverSideControllerKey, id);

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(r);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }

    @Override
    public void upload(byte[] bytes, AsyncCallback<FileLocator> callback) {
        MateuUI.run(new Runnable() {
            @Override
            public void run() {

                try {

                    FileLocator result = ((BaseService)Class.forName("io.mateu.ui.core.server.BaseServiceImpl").newInstance()).upload(bytes);

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onSuccess(result);

                        }
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {

                            callback.onFailure(e);

                        }
                    });

                }

            }
        });

    }
}